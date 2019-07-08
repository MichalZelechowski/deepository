package org.mz.deepository.lego.builder;

import java.util.Locale;

class PovrayExporter {

    private int index = 0;

    String export(Building building) {
        StringBuilder builder = new StringBuilder();
        for (Outline outline : building.outlines()) {
            builder.append(this.export(outline)).append('\n');
        }
        return builder.toString();
    }

    String export(Outline outline) {
        StringBuilder output = new StringBuilder();
        for (Brick brick : outline.bricks()) {
            output.append(String.format(Locale.US, "  object { Brick_%dx1 texture { %s } %s() brick_translate(<%,.1f, %,.1f, %,.1f>) }\n",
                    (int) brick.length(), this.texture(), brick.orientation().toString().toLowerCase(), brick.x(), brick.y(), brick.z()));
        }
        return output.toString();
    }

    private String texture() {
        return Texture.values()[(index++) % Texture.values().length].name();
    }

    static enum Texture {
        medium_stone_grey,
        reddish_brown,
        black,
        bright_red,
        bright_yellow,
        dark_green,
        bright_green,
        yellowish_green,
        sand_yellow,
        dark_stone_grey,
        bright_blue,
        white,
        brick_yellow,
    }

}
