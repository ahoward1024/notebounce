package com.esw.notebounce;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class UserData {

    Type type;
    Edge edge = Edge.noEdge;
    Box.Style style = Box.Style.noBox;

    public enum Type {
        boundary,
        edge,
        ball,
        sim,
        gun,
        box,
    }

    public enum Edge {
        top,
        bot,
        left,
        right,
        noEdge
    }

    UserData(Type type) {
        this.type = type;
    }

    UserData(UserData.Edge edge) {
        this.type = Type.boundary;
        this.edge = edge;
    }

    UserData(Box.Style style, UserData.Edge edge) {
        this.type = Type.box;
        this.style = style;
        this.edge = edge;
    }

    public Type getType() { return type; }

    public Edge getEdge() { return edge; }

    public Box.Style getStyle() { return style; }

    @Override
    public String toString() {
        if(edge == Edge.noEdge) return type.toString();
        if(style == Box.Style.noBox) return type + " | " + edge;
        return style + " | " + edge;
    }
}
