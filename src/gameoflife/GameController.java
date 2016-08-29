package gameoflife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
    private final int width, height, factor;
    private final List<Cell> cells = new ArrayList<>();
    private final List<Integer[]> marks = new ArrayList<>(), checks = new ArrayList<>();
    private final GraphicsContext gc;
    private boolean wrap = false;
    
    public GameController(int width, int height, int factor, GraphicsContext gc, boolean example) {
        this.width = width;
        this.height = height;
        this.factor = factor;
        this.gc = gc;
        if (example) {
            placeExample();
        }
    }
    
    private void placeExample() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File("src\\gameoflife\\exampleTemplate.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int startX = width / 2 - 7, startY = height / 2 - 7;
        for (int y = 0; y < 13; y++) {
            String line = scanner.next();
            for (int x = 0; x < 13; x++) {
                if (line.charAt(x) == 'X') {
                    cells.add(new Cell(x + startX, y + startY));
                }
            }
        }
    }
    
    public void drawGame() {
        gc.setStroke(Color.SILVER);
        gc.setLineWidth(2);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = getCell(x, y);
                if (cell != null) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(x * factor, y * factor, factor, factor);
                gc.strokeRect(x * factor, y * factor, factor, factor);
            }
        }
    }
    
    private Cell getCell(int x, int y) {
        for (Cell cell : cells) {
            if (cell.getX() == x && cell.getY() == y) {
                return cell;
            }
        }
        return null;
    }
    
    public void updateStates() {
        
        cells.stream().forEach((cell) -> {
            int x = cell.getX(), y = cell.getY();
            for (int cx = x - 1; cx < x + 2; cx++) {
                for (int cy = y - 1; cy < y + 2; cy++) {
                    Integer[] coords = {cx, cy};
                    if (!contains(checks, new Integer[] {cx, cy})) {
                        checks.add(coords);
                    }
                }
            }
        });
        
        checks.stream().forEach((coords) -> {
            int x = coords[0], y = coords[1];
            Cell cell = getCell(x, y);
            int adjCells = getAdjCells(x, y);
            if (cell == null) {
                if (adjCells == 3) {
                    marks.add(new Integer[] {x, y});
                }
            } else {
                if (adjCells < 2 || adjCells > 3) {
                    marks.add(new Integer[] {x, y});
                }
            }
        });  
        
        marks.stream().forEach((coords) -> {
            toggleCell(coords[0], coords[1]);
        });
        
        marks.clear();
    }
    
    private int getAdjCells(int x, int y) {
        int sum = 0;
        for (int cx = x - 1; cx < x + 2; cx++) {
            for (int cy = y - 1; cy < y + 2; cy++) {
                if (!(cx == x && cy == y)) {
                    if (wrap) {
                        if (getCell(getWrapped(cx, width), getWrapped(cy, height)) != null) {
                            sum++;
                        }
                    } else {
                        if (getCell(cx, cy) != null) {
                            sum++;
                        }
                    }
                }
            }
        }
        return sum;
    }
    
    private int getWrapped(int c, int len) {
        if (c < 0) {
            return c + len;
        } else if (c >= len) {
            return c - len;
        } else {
            return c;
        }
    }
    
    public void toggleCell(int x, int y) {
        Cell cell = getCell(x, y);
        if (cell == null) {
            cells.add(new Cell(x, y));
        } else {
            cells.remove(cell);
        }
    }
    
    private boolean contains(List<Integer[]> list, Integer[] elem) {
        return list.stream().anyMatch((listElem) -> (Arrays.equals(listElem, elem)));
    }
    
    public void clear() {
        cells.clear();
    }

    public void toggleWrapped() {
        wrap = !wrap;
    }
}
