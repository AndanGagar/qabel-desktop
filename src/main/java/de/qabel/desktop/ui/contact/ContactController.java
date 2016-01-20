package de.qabel.desktop.ui.contact;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.qabel.core.config.Contact;
import de.qabel.core.config.Identity;
import de.qabel.core.exceptions.QblDropInvalidURL;
import de.qabel.desktop.config.ClientConfiguration;
import de.qabel.desktop.repository.ContactRepository;
import de.qabel.desktop.repository.exception.EntityNotFoundExcepion;
import de.qabel.desktop.repository.exception.PersistenceException;
import de.qabel.desktop.ui.AbstractController;
import de.qabel.desktop.ui.accounting.GsonContact;
import de.qabel.desktop.ui.contact.item.BlankItemView;
import de.qabel.desktop.ui.contact.item.ContactItemView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ContactController extends AbstractController implements Initializable {

	ResourceBundle resourceBundle;
	List<ContactItemView> itemViews = new LinkedList<>();
	Identity i;

	@FXML
	Pane contactList;

	@Inject
	private ClientConfiguration clientConfiguration;

	@Inject
	private ContactRepository contactRepository;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resourceBundle = resources;
		buildGson();
		createObserver();
		try {
			loadContacts();
		} catch (EntityNotFoundExcepion entityNotFoundExcepion) {
			entityNotFoundExcepion.printStackTrace();
		}
	}

	@FXML
	protected void handleImportContactsButtonAction(ActionEvent event) throws IOException, PersistenceException, URISyntaxException, QblDropInvalidURL, EntityNotFoundExcepion {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(resourceBundle.getString("downloadFolder"));
		File file = chooser.showOpenDialog(contactList.getScene().getWindow());
		importContacts(file);
		loadContacts();
	}

	@FXML
	protected void handleExportContactsButtonAction(ActionEvent event) throws EntityNotFoundExcepion, IOException {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Download");
		chooser.setInitialFileName("Contacts.json");
		File file = chooser.showSaveDialog(contactList.getScene().getWindow());
		exportContacts(file);
	}

	void loadContacts() throws EntityNotFoundExcepion {
		i = clientConfiguration.getSelectedIdentity();
		contactList.getChildren().clear();
		String old = null;
		List<Contact> contacts = contactRepository.findAllContactFromOneIdentity(i);

		contacts.sort((c1, c2) -> c1.getAlias().compareTo(c2.getAlias()));

		for (Contact co : contacts) {
			if (old == null || !old.equals(co.getAlias().substring(0, 1).toUpperCase())) {
				old = createBlankItem(co);
			}
			createContactItem(co);
		}
	}

	private void createContactItem(Contact co) {
		final Map<String, Object> injectionContext = new HashMap<>();
		injectionContext.put("contact", co);
		ContactItemView itemView = new ContactItemView(injectionContext::get);
		contactList.getChildren().add(itemView.getView());
		itemViews.add(itemView);
	}

	private String createBlankItem(Contact co) {
		String old = co.getAlias().substring(0, 1).toUpperCase();
		final Map<String, Object> injectionContext = new HashMap<>();
		injectionContext.put("contact", co);
		BlankItemView itemView = new BlankItemView(injectionContext::get);
		contactList.getChildren().add(itemView.getView());
		itemViews.add(itemView);
		return old;
	}

	private void createObserver() {
		clientConfiguration.addObserver((o, arg) -> {
			if (!(arg instanceof Identity)) {
				return;
			}
			try {
				loadContacts();
			} catch (EntityNotFoundExcepion entityNotFoundExcepion) {
				entityNotFoundExcepion.printStackTrace();
			}
		});
	}

	void exportContacts(File file) throws EntityNotFoundExcepion, IOException {
		List<Contact> contacts = contactRepository.findAllContactFromOneIdentity(i);
		List<GsonContact> list = new LinkedList<>();


		for (Contact c : contacts) {
			GsonContact gc = createGsonFromEntity(c);
			list.add(gc);
		}

		String jsonContacts = gson.toJson(list);
		writeStringInFile(jsonContacts, file);
	}

	void importContacts(File file) throws IOException, URISyntaxException, QblDropInvalidURL, PersistenceException {
		String content = readFile(file);
		JsonArray list = gson.fromJson(content, JsonArray.class);

		for (JsonElement json : list) {
			GsonContact gc = gson.fromJson(json, GsonContact.class);
			Contact c = gsonContactToContact(gc, i);
			contactRepository.save(c);
		}
	}
}



