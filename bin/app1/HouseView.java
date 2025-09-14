package example;

import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HouseView extends GridWorldView {
    HouseModel hmodel;
    private boolean kitTaken = false;
    private boolean extinguisherActivated = false;

    public HouseView(HouseModel model) {
        super(model, "Simulazione risoluzione incendio", 700);
        this.hmodel = model;
        defaultFont = new Font("Arial", Font.BOLD, 16);
        setVisible(true);
        repaint();
    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        Location bobLoc = hmodel.getAgPos(0);
        Location aliceLoc = hmodel.getAgPos(1);

        switch (object) {
            case HouseModel.PERSON:
                drawPerson(g, x, y, bobLoc);
                break;
            case HouseModel.FIRE1:
            case HouseModel.FIRE2:
                drawStaticFire(g, x, y, aliceLoc);
                break;
            case HouseModel.FIRE_PERIODIC1:
            case HouseModel.FIRE_PERIODIC2:
            case HouseModel.FIRE_PERIODIC3:
                drawPeriodicFire(g, x, y, aliceLoc);
                break;
            case HouseModel.EXTINGUISHER:
                drawExtinguisher(g, x, y, aliceLoc);
                break;
            case HouseModel.KIT:
                drawKit(g, x, y, bobLoc);
                break;
        }
    }

    private void drawPerson(Graphics g, int x, int y, Location bobLoc) {
        if (bobLoc.equals(hmodel.lPerson)) {
            super.drawAgent(g, x, y, Color.green, -1);
        } else {
            g.setColor(Color.magenta);
            super.drawAgent(g, x, y, Color.pink, -1);
        }
        drawString(g, x, y, defaultFont, "P");
    }

    private void drawStaticFire(Graphics g, int x, int y, Location aliceLoc) {
        Location fireLoc = new Location(x, y);
        if (!aliceLoc.equals(fireLoc)) {
            super.drawAgent(g, x, y, Color.red, -1);
        } else {
            super.drawAgent(g, x, y, Color.blue, -1);
        }
        drawString(g, x, y, defaultFont, "F");
    }

    private void drawPeriodicFire(Graphics g, int x, int y, Location aliceLoc) {
        Location fireLoc = new Location(x, y);
        Color fireColor;
        
        if (aliceLoc.equals(fireLoc)) {
            fireColor = Color.blue;
        } else {
            fireColor = Color.orange;
        }
        
        g.setColor(fireColor);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }

    private void drawExtinguisher(Graphics g, int x, int y, Location aliceLoc) {
        if (!extinguisherActivated) {
            g.setColor(Color.gray);
            drawString(g, x, y, defaultFont, "estintore");
        }
        if (aliceLoc.equals(hmodel.lExtinguisher)) {
            extinguisherActivated = true;
        }
    }

    private void drawKit(Graphics g, int x, int y, Location bobLoc) {
        if (!kitTaken) {
            g.setColor(Color.GREEN);
            drawString(g, x, y, defaultFont, "Kit");
        }
        if (bobLoc.equals(hmodel.lKit)) {
            kitTaken = true;
        }
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        if (id == 0) {
            g.setColor(Color.black);
            super.drawString(g, x, y, defaultFont, "Bob");
        } else if (id == 1) {
            g.setColor(Color.black);
            super.drawString(g, x, y, defaultFont, "Alice");
        }
    }
}


