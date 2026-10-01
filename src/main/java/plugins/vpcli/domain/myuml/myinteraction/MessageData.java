package plugins.vpcli.domain.myuml.myinteraction;


import plugins.vpcli.domain.myuml.mycommon.MyRelationship;

public class MessageData extends MyRelationship {

	private String sequenceNumber;
	private boolean isReply;
	private boolean isCreate;
	private boolean isDestroy;
	private boolean isRecursive;
	private MyLifeline createdLifeline;
	private boolean isDuration;
	private int durationHeight;
	private boolean isLost;

    public MessageData(String source, String target, String type, String name) {
		super(source, target, type, name);
	}

	public String toExportFormat(boolean activate, String indent) {
		String symbol = isReply ? "-->" : "->";
		String activationString = "";
		if(this.isDuration) symbol = symbol.concat("(" + durationHeight + ")"); 
		if(this.isDestroy) symbol = symbol.concat("x"); 
		if(this.isRecursive || activate) activationString = " ++";
		String label = (sequenceNumber == null) ? "" : (sequenceNumber + " ");

		String prefix = (!label.isEmpty() || !getName().isEmpty()) ? " : " : "";

		String sourceFormatted = formatAlias(getSource());
		String targetFormatted = formatAlias(getTarget());

		// Special formatting for lost/found
		if ("[".equals(getSource())) {
			sourceFormatted = "[o";
			return indent + sourceFormatted + symbol + " " + targetFormatted + activationString + prefix + label + getName() + "\n";
		}
		if ("]".equals(getTarget())) {
			targetFormatted = "o]";
			return  indent + sourceFormatted + " " + symbol + targetFormatted + activationString + prefix + label + getName() + "\n";
		}
		
		String messageLine = indent + sourceFormatted + " " + symbol + " " + targetFormatted + activationString + prefix + label + getName() + "\n";
		
		if (this.isRecursive)
			return messageLine + indent + "deactivate " + targetFormatted + "\n";
		return messageLine;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	public boolean isCreate() {
		return isCreate;
	}

	public void setCreate(boolean isCreate, MyLifeline createdMyLifeline) {
		this.isCreate = isCreate;
		setCreatedLifeline(createdMyLifeline);
	}

	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
	}

	public MyLifeline getCreatedLifeline() {
		return createdLifeline;
	}

	public void setCreatedLifeline(MyLifeline createdLifeline) {
		this.createdLifeline = createdLifeline;
	}

	public void setDuration(int durationHeight) {
		this.isDuration = true;
		this.durationHeight = durationHeight;

	}

	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}

	public void setLost(boolean lost) {
        isLost = lost;
    }

	public void setFound(boolean found) {
    }
}
