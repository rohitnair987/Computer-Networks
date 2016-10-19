public class PCAPGlobalHeader {
	long Snaplength;
	LinkLayerProtocolEnum LinkLayerProtocol;
	
	public PCAPGlobalHeader() {
		Snaplength = 0;
		LinkLayerProtocol = LinkLayerProtocolEnum.OTHER;
	}
}
