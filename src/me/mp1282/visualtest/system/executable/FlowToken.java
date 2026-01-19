package me.mp1282.visualtest.system.executable;

public enum FlowToken {
    INPUT(-1),
    OUTPUT(1)

    ;

    /* Direction of port (-1 for input, 1 for output */
    private final int direction;

    FlowToken(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }
}
