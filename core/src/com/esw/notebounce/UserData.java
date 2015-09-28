package com.esw.notebounce;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class UserData {

    Type type;
    Edge edge = Edge.noEdge;
    Box.Color color = Box.Color.none;
    Box.Shade shade = Box.Shade.zero;

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

    UserData(Box.Color style, UserData.Edge edge) {
        this.type = Type.box;
        this.color = style;
        this.edge = edge;
    }

    public Type getType() { return type; }

    public Edge getEdge() { return edge; }

    public Box.Color getStyle() { return color; }

    public Box.Shade getShade() { return shade; }

    @Override
    public String toString() {
        if(edge == Edge.noEdge) return type.toString();
        if(color == Box.Color.none) return type + " | " + edge;
        return color + " | " + edge;
    }
}
