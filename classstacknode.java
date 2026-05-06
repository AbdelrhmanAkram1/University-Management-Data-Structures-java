class StackNode {
    private Action action;
    private StackNode next;

    public StackNode(Action action) {
        this.action = action;
        this.next = null;
    }

    public Action getAction() {
        return action;
    }

    public StackNode getNext() {
        return next;
    }

    public void setNext(StackNode next) {
        this.next = next;
    }
}
