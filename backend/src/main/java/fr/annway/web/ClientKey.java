package fr.annway.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Clé d'un client pour la limite de débit : son adresse tronquée (réseau /24 en IPv4, /64 en
 * IPv6), jamais l'adresse complète, qui est une donnée personnelle ([PERSONAL-DATA]).
 */
public final class ClientKey {

    private static final int IPV4_KEPT_BYTES = 3;
    private static final int IPV6_KEPT_BYTES = 8;
    private static final int IPV4_BYTES = 4;

    private ClientKey() {
    }

    public static String of(InetAddress address) {
        Objects.requireNonNull(address, "address");
        var bytes = address.getAddress();
        var keptBytes = bytes.length == IPV4_BYTES ? IPV4_KEPT_BYTES : IPV6_KEPT_BYTES;
        Arrays.fill(bytes, keptBytes, bytes.length, (byte) 0);
        try {
            return InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException impossible) {
            throw new IllegalStateException("adresse de longueur invalide", impossible);
        }
    }
}
