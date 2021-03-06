package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.util.encoders.Hex;

public class KeyFactorySpi
    extends BaseKeyFactorySpi
    implements AsymmetricKeyInfoConverter
{
    static final byte[] x448Prefix = Hex.decode("3042300506032b656f033900");
    static final byte[] x25519Prefix = Hex.decode("302a300506032b656e032100");
    static final byte[] Ed448Prefix = Hex.decode("3043300506032b6571033a00");
    static final byte[] Ed25519Prefix = Hex.decode("302a300506032b6570032100");

    private static final byte x448_type = 0x6f;
    private static final byte x25519_type = 0x6e;
    private static final byte Ed448_type = 0x71;
    private static final byte Ed25519_type = 0x70;

    String algorithm;
    private final boolean isXdh;
    private final int specificBase;

    public KeyFactorySpi(
        String algorithm,
        boolean isXdh,
        int     specificBase)
    {
        this.algorithm = algorithm;
        this.isXdh = isXdh;
        this.specificBase = specificBase;
    }

    protected Key engineTranslateKey(
        Key    key)
        throws InvalidKeyException
    {
        throw new InvalidKeyException("key type unknown");
    }

    protected KeySpec engineGetKeySpec(
        Key    key,
        Class    spec)
    throws InvalidKeySpecException
    {
       return super.engineGetKeySpec(key, spec);
    }

    protected PrivateKey engineGeneratePrivate(
        KeySpec keySpec)
        throws InvalidKeySpecException
    {
        return super.engineGeneratePrivate(keySpec);
    }

    protected PublicKey engineGeneratePublic(
        KeySpec keySpec)
        throws InvalidKeySpecException
    {
        if (keySpec instanceof X509EncodedKeySpec)
        {
            byte[] enc = ((X509EncodedKeySpec)keySpec).getEncoded();
            // optimise if we can
            if (specificBase == 0 || specificBase == enc[8])
            {
                switch (enc[8])
                {
                case x448_type:
                    return new BCXDHPublicKey(x448Prefix, enc);
                case x25519_type:
                    return new BCXDHPublicKey(x25519Prefix, enc);
                case Ed448_type:
                    return new BCEdDSAPublicKey(Ed448Prefix, enc);
                case Ed25519_type:
                    return new BCEdDSAPublicKey(Ed25519Prefix, enc);
                default:
                    return super.engineGeneratePublic(keySpec);
                }
            }
        }

        return super.engineGeneratePublic(keySpec);
    }

    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo)
        throws IOException
    {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();

        if (isXdh)
        {
            if ((specificBase == 0 || specificBase == x448_type) && algOid.equals(EdECObjectIdentifiers.id_X448))
            {
                return new BCXDHPrivateKey(keyInfo);
            }
            if ((specificBase == 0 || specificBase == x25519_type) && algOid.equals(EdECObjectIdentifiers.id_X25519))
            {
                return new BCXDHPrivateKey(keyInfo);
            }
        }
        else if (algOid.equals(EdECObjectIdentifiers.id_Ed448) || algOid.equals(EdECObjectIdentifiers.id_Ed25519))
        {
            if ((specificBase == 0 || specificBase == Ed448_type) && algOid.equals(EdECObjectIdentifiers.id_Ed448))
            {
                return new BCEdDSAPrivateKey(keyInfo);
            }
            if ((specificBase == 0 || specificBase == Ed25519_type) && algOid.equals(EdECObjectIdentifiers.id_Ed25519))
            {
                return new BCEdDSAPrivateKey(keyInfo);
            }
        }

        throw new IOException("algorithm identifier " + algOid + " in key not recognized");
    }

    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo)
        throws IOException
    {
        ASN1ObjectIdentifier algOid = keyInfo.getAlgorithm().getAlgorithm();

        if (isXdh)
        {
            if ((specificBase == 0 || specificBase == x448_type) && algOid.equals(EdECObjectIdentifiers.id_X448))
            {
                return new BCXDHPublicKey(keyInfo);
            }
            if ((specificBase == 0 || specificBase == x25519_type) && algOid.equals(EdECObjectIdentifiers.id_X25519))
            {
                return new BCXDHPublicKey(keyInfo);
            }
        }
        else if (algOid.equals(EdECObjectIdentifiers.id_Ed448) || algOid.equals(EdECObjectIdentifiers.id_Ed25519))
        {
            if ((specificBase == 0 || specificBase == Ed448_type) && algOid.equals(EdECObjectIdentifiers.id_Ed448))
            {
                return new BCEdDSAPublicKey(keyInfo);
            }
            if ((specificBase == 0 || specificBase == Ed25519_type) && algOid.equals(EdECObjectIdentifiers.id_Ed25519))
            {
                return new BCEdDSAPublicKey(keyInfo);
            }
        }

        throw new IOException("algorithm identifier " + algOid + " in key not recognized");
    }

    public static class XDH
        extends KeyFactorySpi
    {
        public XDH()
        {
            super("XDH", true, 0);
        }
    }

    public static class X448
        extends KeyFactorySpi
    {
        public X448()
        {
            super("X448", true, x448_type);
        }
    }

    public static class X25519
        extends KeyFactorySpi
    {
        public X25519()
        {
            super("X25519", true, x25519_type);
        }
    }

    public static class EDDSA
        extends KeyFactorySpi
    {
        public EDDSA()
        {
            super("EdDSA", false, 0);
        }
    }

    public static class ED448
        extends KeyFactorySpi
    {
        public ED448()
        {
            super("Ed448", false, Ed448_type);
        }
    }

    public static class ED25519
        extends KeyFactorySpi
    {
        public ED25519()
        {
            super("Ed25519", false, Ed25519_type);
        }
    }
}