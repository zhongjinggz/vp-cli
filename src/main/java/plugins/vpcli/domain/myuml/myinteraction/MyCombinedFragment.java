package plugins.vpcli.domain.myuml.myinteraction;

import java.util.ArrayList;
import java.util.List;

public class MyCombinedFragment {


    private String type; // loop, alt, group ..
    private List<Operand> operands = new ArrayList<>();

    public MyCombinedFragment(String type) {
        this.type = type;
    }

    public void setNestedCombinedFragments(List<MyCombinedFragment> nestedCombinedFragments) {
    }

    public List<Operand> getOperands() {
        return operands;
    }
    public void setOperands(List<Operand> operands) {
        this.operands = operands;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public static class Operand {
        private List<MessageData> messages = new ArrayList<>();
        private String label;

        public List<MessageData> getMessages() {
            return messages;
        }
        public void setMessages(List<MessageData> messages) {
            this.messages = messages;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
    }
}