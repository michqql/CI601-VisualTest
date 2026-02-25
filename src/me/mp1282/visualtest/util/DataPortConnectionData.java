package me.mp1282.visualtest.util;

import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.executable.DataPort;

import java.util.Objects;

public record DataPortConnectionData(DiagramNode nodeA, DiagramNode nodeB, DataPort portA, DataPort portB) {

    public DataPortConnectionData(DiagramNode nodeA, DiagramNode nodeB, DataPort portA, DataPort portB) {
        if (nodeA.getUniqueId().compareTo(nodeB.getUniqueId()) < 0) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.portA = portA;
            this.portB = portB;
        } else {
            this.nodeA = nodeB;
            this.nodeB = nodeA;
            this.portA = portB;
            this.portB = portA;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPortConnectionData that)) return false;

        return Objects.equals(portA, that.portA) && Objects.equals(portB, that.portB) &&
                Objects.equals(nodeA, that.nodeA) && Objects.equals(nodeB, that.nodeB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeA, nodeB, portA, portB);
    }
}
