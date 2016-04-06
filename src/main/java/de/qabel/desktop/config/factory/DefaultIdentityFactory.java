package de.qabel.desktop.config.factory;

import de.qabel.core.config.Identity;
import de.qabel.core.crypto.QblECKeyPair;
import de.qabel.core.drop.DropURL;

import java.util.Collection;

public class DefaultIdentityFactory implements IdentityFactory {
    @Override
    public Identity createIdentity(QblECKeyPair keyPair, Collection<DropURL> dropURLs, String alias) {
    return new Identity(alias, dropURLs, keyPair);
    }
}
