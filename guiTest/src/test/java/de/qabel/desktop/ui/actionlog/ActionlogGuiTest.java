package de.qabel.desktop.ui.actionlog;

import com.airhacks.afterburner.views.FXMLView;
import de.qabel.core.config.Contact;
import de.qabel.core.config.Identity;
import de.qabel.core.drop.DropMessage;
import de.qabel.desktop.daemon.drop.TextMessage;
import de.qabel.desktop.repository.DropMessageRepository;
import de.qabel.desktop.ui.AbstractGuiTest;
import javafx.scene.input.KeyCode;
import org.junit.Test;
import org.testfx.api.FxRobot;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ActionlogGuiTest extends AbstractGuiTest<ActionlogController> {
    private AtomicReference<String> browserOpener = new AtomicReference<>();

    @Override
    protected FXMLView getView() {
        return new ActionlogView();
    }

    @Test
    public void typeMessageWithHyperlinkAndClickIt() throws Exception {
        controller.contact = new Contact(identity.getAlias(), identity.getDropUrls(), identity.getEcPublicKey());
        writeTwoLinesWithOneHyperlink();
        submitChat();

        plaintextMessageRenderer.getHyperlinkRenderer().setBrowserOpener(browserOpener::set);
        fxMessageRendererFactory.setFallbackRenderer(plaintextMessageRenderer);
        clickOn(".hyperlink");
        assertThat(browserOpener.get(), is("http://qabel.de"));
    }

    private void submitChat() {
        assertTrue(receiveMessages().isEmpty());
        robot.push(KeyCode.ENTER);
        assertFalse(receiveMessages().isEmpty());
    }

    private void writeTwoLinesWithOneHyperlink() {
        waitUntil(() -> controller.textarea != null);
        clickOn("#textarea");
        controller.textarea.setText(
            "Minions ipsum pepete underweaaar daa hahaha potatoooo tatata bala tu ti aamoo! Aaaaaah bappleees chasy. Potatoooo tulaliloo potatoooo chasy bananaaaa ti aamoo!\n" +
                "http://qabel.de"
        );

    }

    @Test
    public void testSendMessage() {
        String text = "Message";
        waitUntil(() -> controller.textarea != null);
        Identity i = controller.identity;
        controller.contact = new Contact(i.getAlias(), i.getDropUrls(), i.getEcPublicKey());
        clickOn("#textarea").write(text);
        robot.push(KeyCode.ENTER);
        List<DropMessage> list = receiveMessages();
        assertEquals(1, list.size());
        assertEquals(text, TextMessage.fromJson(list.get(0).getDropPayload()).getText());
        assertEquals(new Date().getTime(), list.get(0).getCreationDate().getTime(), 100000);
        assertEquals(controller.identity.getId(), list.get(0).getSender().getId());
    }

    private List<DropMessage> receiveMessages() {
        return controller.dropConnector.receive(controller.identity, new Date(0L)).dropMessages;
    }

    @Test
    public void multilineInput() {
        controller.contact = new Contact(identity.getAlias(), identity.getDropUrls(), identity.getEcPublicKey());
        FxRobot textArea = clickOn("#textarea");
        textArea.write("line1");
        robot.press(KeyCode.SHIFT);
        try {
            robot.push(KeyCode.ENTER);
        } finally {
            robot.release(KeyCode.SHIFT);
        }
        robot.write("line2");

        submitChat();

        DropMessage message = receiveMessages().get(0);
        assertEquals(DropMessageRepository.PAYLOAD_TYPE_MESSAGE, message.getDropPayloadType());
        assertEquals("line1\nline2", TextMessage.fromJson(message.getDropPayload()).getText());
    }

    @Test
    public void testSendMessageWithoutContent() {
        waitUntil(() -> controller.textarea != null);
        robot.push(KeyCode.ENTER);
        List<DropMessage> list = receiveMessages();
        assertEquals(0, list.size());
    }
}
