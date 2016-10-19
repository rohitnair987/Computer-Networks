

public class IPHeader {
	// 4 bits
	int Version;

	// 4 bits
	int HeaderLength;

	// (8 bits)
	// TypeOfService - skip

	// (16 bits)
	int TotalLength;

	/*
	 * (16 bits) used for uniquely identifying the IP datagrams. This value is
	 * incremented every-time an IP datagram is sent from source to the
	 * destination. This field comes in handy while reassembly of fragmented IP
	 * data grams.
	 */
	int Identification;

	// (1 bit) Reserved
	boolean FlagReserved;

	/*
	 * (1 bit) When this bit is set then IP datagram is never fragmented, rather
	 * its thrown away if a requirement for fragment arises.
	 */
	boolean FlagDontFragment;

	/*
	 * (1 bit) If this bit is set then it represents a fragmented IP datagram
	 * that has more fragments after it. In case of last fragment of an IP
	 * datagram this bit is not set signifying that this is the last fragment of
	 * a particular IP datagram.
	 */
	boolean MoreFragment;

	/*
	 * (13 bits): In case of fragmented IP data grams, this field contains the
	 * offset( in terms of 8 bytes units) from the start of IP datagram. So
	 * again, this field is used in reassembly of fragmented IP datagrams.
	 */
	int FragmentOffset;

	/*
	 * (8 bits) : This value represents number of hops that the IP datagram will
	 * go through before being discarded.
	 */
	int TTL;

	/*
	 * (8 bits) : This field represents the transport layer protocol that handed
	 * over data to IP layer.
	 */
	ConstantsEnum TransportLayerProtocol;

	/*
	 * (16 bits) : This fields represents a value that is calculated using an
	 * algorithm covering all the fields in header (assuming this very field to
	 * be zero)
	 */
	int HeaderChecksum;

	// 32 bits
	String Source;

	// 32 bits
	String Destination;

	// Options - create a class for this

}
