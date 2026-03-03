
// === นำเข้าไลบรารีที่จำเป็น ===
import java.awt.*; // สำหรับจัดการ GUI พื้นฐาน เช่น สี, ฟอนต์, เลย์เอาต์
import java.awt.event.*; // สำหรับจัดการเหตุการณ์ต่างๆ เช่น การคลิก, การพิมพ์
import java.util.concurrent.ExecutionException; // ไลบรารี GUI หลัก เช่น JFrame, JPanel, JTextField, JButton
import javax.swing.*;

public class SudokuGame extends JFrame {

    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    // กำหนดสีต่างๆ สำหรับ UI ==
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CELL = new Color(30, 41, 59);
    private static final Color BG_CELL_SELECTED = new Color(56, 89, 160);
    private static final Color BG_CELL_HIGHLIGHT = new Color(39, 55, 85);
    private static final Color BG_CELL_ERROR = new Color(120, 30, 30);
    private static final Color TEXT_FIXED = new Color(148, 163, 184);
    private static final Color TEXT_USER = new Color(96, 165, 250);
    private static final Color TEXT_WHITE = new Color(226, 232, 240);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color BORDER_THICK = new Color(100, 116, 139);
    private static final Color BORDER_THIN = new Color(51, 65, 85);

    // ฟีเจอร์หลัก ==
    private final int[][] playerBoard = new int[SIZE][SIZE]; // เก็บตัวเลขปัจจุบันที่อยู่บนหน้าจอ (ที่ผู้เล่นพิมพ์ลงไป)
    private final JTextField[][] cells = new JTextField[SIZE][SIZE]; // เก็บออบเจกต์ JTextField (กล่องข้อความ) ทั้ง 81
                                                               // ช่องบนหน้าจอ เพื่อให้เราสั่งเปลี่ยนสีหรือดึงข้อความได้
    private int selectedRow = -1, selectedCol = -1; // เก็บตำแหน่งของช่องที่ผู้เล่นกำลังเลือกอยู่ (เริ่มต้นเป็น -1
                                                    // คือยังไม่เลือกอะไรเลย)
    private JLabel statusLabel; // ป้ายข้อความ (Label) สำหรับแสดงสถานะ ด้านบนของจอ
    private JLabel filledLabel; // แสดงจำนวนช่องที่กรอกแล้ว เช่น "Filled: 25/81"

    // สำหรับจัดการหน้าต่าง (Start Menu & Game)
    // ตัวจัดการหน้าจอ (Layout) ที่ช่วยให้เราสลับไปมาระหว่างหน้า "Menu" กับหน้า
    // "Game" ได้โดยไม่ต้องเปิดหน้าต่างโปรแกรมใหม่
    private final CardLayout cardLayout;
    private final JPanel mainContentPanel;

    // ทำหน้าที่ตั้งค่าหน้าต่างโปรแกรม (JFrame), เรียกใช้ CardLayout, และนำหน้า Menu
    // กับหน้า Game มาแปะรวมกัน
    public SudokuGame() {
        setTitle("🧩 Sudoku Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // ตั้งให้โปรแกรมปิดเมื่อกดปุ่มกากบาท
        setResizable(false); // ห้ามแก้ขนาดหน้าต่างเพื่อให้ UI ไม่เสียรูป

        // ใช้ CardLayout เพื่อสลับหน้าระหว่าง Menu กับ Game
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // 1. สร้างหน้า Main Menu
        mainContentPanel.add(createMenuPanel(), "MENU");

        // 2. สร้างหน้า Game Play
        JPanel gamePanel = new JPanel(new BorderLayout(0, 0));
        gamePanel.setBackground(BG_PRIMARY);
        gamePanel.add(createTopPanel(), BorderLayout.NORTH);
        gamePanel.add(createBoardPanel(), BorderLayout.CENTER);
        gamePanel.add(createBottomPanel(), BorderLayout.SOUTH);
        mainContentPanel.add(gamePanel, "GAME");

        add(mainContentPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // เริ่มต้นที่หน้า Menu ==
        cardLayout.show(mainContentPanel, "MENU");
    }

    // ============== สร้างหน้า Start Menu ==============
    private JPanel createMenuPanel() {
        JPanel menu = new JPanel();
        menu.setBackground(BG_PRIMARY);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        menu.add(Box.createVerticalGlue());

        // --- เริ่มต้นส่วนของข้อความ Title แบบคลื่น ---
        JPanel waveTitlePanel = new JPanel() {
            private int angle = 0;
            private final String text = "SUDOKU";

            { // Block เริ่มต้นการทำงานของ Timer
                setOpaque(false);
                setPreferredSize(new Dimension(400, 80));
                setMaximumSize(new Dimension(400, 80));

                // Timer สั่งให้วาดใหม่ทุกๆ 50 มิลลิวินาที
                Timer waveTimer = new Timer(50, e -> {
                    angle = (angle + 10) % 360; // เพิ่มมุมไปเรื่อยๆ เพื่อสร้างคลื่น
                    repaint();
                });
                waveTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // ตั้งค่าให้ตัวอักษรคมชัด
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("SansSerif", Font.BOLD, 48));
                g2.setColor(TEXT_WHITE);

                FontMetrics fm = g2.getFontMetrics();
                int startX = (getWidth() - fm.stringWidth(text)) / 2;
                int baseY = getHeight() / 2 + fm.getAscent() / 4;

                int currentX = startX;
                // วาดตัวอักษรทีละตัว
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    // คำนวณความสูงของคลื่นด้วย Sine (คูณ 8 คือความสูงคลื่น, i * 30
                    // คือระยะห่างคลื่นแต่ละตัว)
                    int yOffset = (int) (Math.sin(Math.toRadians(angle + (i * 30))) * 10);

                    g2.drawString(String.valueOf(c), currentX, baseY + yOffset);
                    currentX += fm.charWidth(c);
                }
                g2.dispose();
            }
        };
        waveTitlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(waveTitlePanel);
        // --- จบส่วนของข้อความ Title แบบคลื่น ---

        menu.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton startBtn = createStyledButton("▶ Start Game");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        startBtn.setMaximumSize(new Dimension(200, 50));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // เมื่อกด Start ให้เข้าสู่เกมโหมด Custom โดยตรง
        startBtn.addActionListener(e -> startCustomGame());
        menu.add(startBtn);

        menu.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton exitBtn = createStyledButton("🚪 Exit");
        exitBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        exitBtn.setMaximumSize(new Dimension(200, 50));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // เมื่อกด Exit ให้ปิดโปรแกรม
        exitBtn.addActionListener(e -> System.exit(0));
        menu.add(exitBtn);

        menu.add(Box.createVerticalGlue());

        return menu;
    }

    // ============== ส่วน UI ของหน้าเกม ==============

    // สร้างแถบด้านบน: แสดงชื่อเกม และสถานะ
    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PRIMARY);
        top.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel title = new JLabel("SUDOKU", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_WHITE);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        infoPanel.setBackground(BG_PRIMARY);

        statusLabel = new JLabel("Custom - Input puzzle, press Solve");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(ACCENT);

        filledLabel = new JLabel("📝 Filled: 0/81");
        filledLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        filledLabel.setForeground(TEXT_FIXED);

        infoPanel.add(statusLabel);
        infoPanel.add(filledLabel);

        top.add(title, BorderLayout.WEST);
        top.add(infoPanel, BorderLayout.EAST);
        return top;
    }

    // สร้างกระดาน Sudoku 9x9 โดยแบ่งเป็น 9 กล่องย่อย (3x3) แต่ละกล่องมี 9 ช่อง
    // ใช้ลูปซ้อน 4 ชั้น: กล่องแถว -> กล่องคอลัมน์ -> แถวในกล่อง -> คอลัมน์ในกล่อง
    private JPanel createBoardPanel() {
        JPanel boardWrapper = new JPanel(new GridLayout(SUBGRID, SUBGRID, 3, 3));
        boardWrapper.setBackground(BORDER_THICK);
        boardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 20, 5, 20),
                BorderFactory.createLineBorder(BORDER_THICK, 3)));

        for (int boxRow = 0; boxRow < SUBGRID; boxRow++) {
            for (int boxCol = 0; boxCol < SUBGRID; boxCol++) {
                JPanel subGrid = new JPanel(new GridLayout(SUBGRID, SUBGRID, 1, 1));
                subGrid.setBackground(BORDER_THIN);

                for (int r = 0; r < SUBGRID; r++) {
                    for (int c = 0; c < SUBGRID; c++) {
                        int row = boxRow * SUBGRID + r;
                        int col = boxCol * SUBGRID + c;

                        JTextField cell = new JTextField();
                        cell.setHorizontalAlignment(JTextField.CENTER);
                        cell.setFont(new Font("SansSerif", Font.BOLD, 22));
                        cell.setPreferredSize(new Dimension(55, 55));
                        cell.setBackground(BG_CELL);
                        cell.setForeground(TEXT_USER);
                        cell.setCaretColor(TEXT_USER);
                        cell.setBorder(BorderFactory.createLineBorder(BORDER_THIN, 1));

                        final int fr = row, fc = col; // เก็บค่าตำแหน่งไว้ใน final เพื่อใช้ใน inner class

                        // เมื่อผู้เล่นคลิกเลือกช่องนี้ → จำตำแหน่งไว้ แล้วไฮไลท์ช่องที่เกี่ยวข้อง
                        cell.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent e) {
                                selectedRow = fr;
                                selectedCol = fc;
                                highlightCells();
                            }
                        });

                        // จัดการเมื่อผู้เล่นกดปุ่มบนแป้นพิมพ์ในช่องนี้
                        cell.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                                char ch = e.getKeyChar();
                                // ถ้าผู้เล่นกดเลข 1-9 → บันทึกลง playerBoard แล้วแสดงบนหน้าจอ
                                if (ch >= '1' && ch <= '9') {
                                    e.consume(); // กัน JTextField ไม่ให้ใส่ตัวอักษรซ้ำเอง
                                    int num = ch - '0'; // แปลง char '1'-'9' เป็น int 1-9
                                    playerBoard[fr][fc] = num;
                                    cell.setText(String.valueOf(num));
                                    cell.setForeground(TEXT_USER);

                                    highlightCells(); // อัปเดตสีไฮไลท์
                                    updateFilledCount(); // อัปเดตจำนวนช่องที่กรอกแล้ว
                                    // ถ้ากดเลข 0 หรือปุ่ม Backspace/Delete → ลบตัวเลขออกจากช่อง
                                } else if (ch == '0' || ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) {
                                    e.consume();
                                    playerBoard[fr][fc] = 0;
                                    cell.setText("");
                                    highlightCells();
                                    updateFilledCount();
                                } else {
                                    e.consume(); // กดปุ่มอื่น → ไม่ทำอะไร (ป้องกันตัวอักษรอื่นเข้ามา)
                                }
                            }

                            // จัดการปุ่มพิเศษ: ลบตัวเลข และเลื่อนช่องด้วยปุ่มลูกศร
                            @Override
                            public void keyPressed(KeyEvent e) {
                                int code = e.getKeyCode();
                                // กด Delete/Backspace → ลบตัวเลขออก
                                if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
                                    playerBoard[fr][fc] = 0;
                                    cell.setText("");
                                    highlightCells();
                                    updateFilledCount();
                                    e.consume();
                                }
                                // เลื่อนช่องด้วยปุ่มลูกศร ↑↓←→ (ไม่ต้องคลิกเมาส์)
                                int nr = fr, nc = fc;
                                switch (code) {
                                    case KeyEvent.VK_UP -> nr = Math.max(0, fr - 1);
                                    case KeyEvent.VK_DOWN -> nr = Math.min(8, fr + 1);
                                    case KeyEvent.VK_LEFT -> nc = Math.max(0, fc - 1);
                                    case KeyEvent.VK_RIGHT -> nc = Math.min(8, fc + 1);
                                }
                                if (nr != fr || nc != fc) {
                                    cells[nr][nc].requestFocusInWindow(); // ย้าย focus ไปช่องใหม่
                                }
                            }
                        });

                        cells[row][col] = cell;
                        subGrid.add(cell);
                    }
                }
                boardWrapper.add(subGrid);
            }
        }
        return boardWrapper;
    }

    // สร้างแผงปุ่มด้านล่าง: ปุ่มควบคุม (Menu, Solve)
    private JPanel createBottomPanel() {

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_PRIMARY);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // สร้างปุ่มควบคุมเกม (Menu, Solve)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionPanel.setBackground(BG_PRIMARY);

        JButton menuBtn = createStyledButton("🏠 Menu");
        menuBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "MENU"); // กลับหน้าหลัก
        });

        // ปุ่มล้างกระดาน: ลบตัวเลขทั้งหมดแล้วเริ่มกรอกใหม่
        JButton clearBtn = createStyledButton("🗑 Clear All");
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear the entire board?",
                    "Clear All", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                newCustomGame();
                highlightCells();
                if (statusLabel != null && statusLabel.getParent() != null) {
                    statusLabel.getParent().revalidate();
                    statusLabel.getParent().repaint();
                }
            }
        });

        // ปุ่มดูเฉลย: แสดงคำตอบทั้งหมด (ถามยืนยันก่อน)
        JButton solveBtn = createStyledButton("👁 Solve");
        solveBtn.addActionListener(e -> revealSolution());

        actionPanel.add(menuBtn);
        actionPanel.add(clearBtn);
        actionPanel.add(solveBtn);

        bottom.add(actionPanel, BorderLayout.CENTER);
        return bottom;
    }

    // สร้างปุ่มแบบ Custom สวยๆ: มีมุมโค้ง, เปลี่ยนสีเมื่อ hover/กด
    // ใช้ paintComponent วาดพื้นหลังปุ่มเอง แทนที่จะใช้ปุ่มมาตรฐานของ Swing
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT.darker()); // กำลังกดอยู่ → สีน้ำเงินเข้ม
                } else if (getModel().isRollover()) {
                    g2.setColor(BG_CELL_HIGHLIGHT); // เมาส์ชี้อยู่ → สีไฮไลท์
                } else {
                    g2.setColor(BG_CELL); // ปกติ → สีพื้นหลังช่อง
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // วาดสี่เหลี่ยมมุมโค้ง
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(TEXT_WHITE);
        btn.setContentAreaFilled(false); // ปิดพื้นหลังปุ่มมาตรฐาน (เราวาดเอง)
        btn.setBorderPainted(false); // ปิดขอบปุ่มมาตรฐาน
        btn.setFocusPainted(false); // ปิดเส้นประรอบปุ่มเมื่อ focus
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // เปลี่ยนเคอร์เซอร์เป็นมือเมื่อชี้
        return btn;
    }

    // ไฮไลท์ช่องทั้ง 81 ช่องตามเงื่อนไข (เรียกทุกครั้งที่มีการเปลี่ยนแปลงบนกระดาน)
    // ลำดับความสำคัญ: ช่องที่เลือก > แถว/คอลัมน์/กล่องเดียวกัน > เลขเดียวกัน >
    // ช่องผิด
    private void highlightCells() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Color bg;
                if (r == selectedRow && c == selectedCol) {
                    bg = BG_CELL_SELECTED; // ช่องที่กำลังเลือกอยู่ → สีน้ำเงินเข้ม
                } else if (r == selectedRow || c == selectedCol ||
                        (r / SUBGRID == selectedRow / SUBGRID && c / SUBGRID == selectedCol / SUBGRID)) {
                    bg = BG_CELL_HIGHLIGHT; // อยู่แถว/คอลัมน์/กล่อง 3x3 เดียวกัน → สีไฮไลท์
                } else {
                    bg = BG_CELL; // ช่องว่างทั่วไป → สีปกติ
                }

                // ไฮไลท์ช่องที่มีตัวเลขเดียวกันกับช่องที่เลือก (ช่วยให้เห็นตัวเลขซ้ำ)
                if (selectedRow >= 0 && selectedCol >= 0 && playerBoard[selectedRow][selectedCol] != 0
                        && playerBoard[r][c] == playerBoard[selectedRow][selectedCol]
                        && !(r == selectedRow && c == selectedCol)) {
                    bg = BG_CELL_HIGHLIGHT.brighter();
                }

                // ถ้าช่องนี้มีเลขซ้ำตามกฎ Sudoku → แสดงสีแดง
                if (playerBoard[r][c] != 0 && hasDuplicate(r, c)) {
                    bg = BG_CELL_ERROR;
                }

                cells[r][c].setBackground(bg);
            }
        }
    }

    // นับจำนวนช่องที่กรอกแล้ว แล้วอัปเดตแสดงผล
    private void updateFilledCount() {
        int count = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (playerBoard[r][c] != 0)
                    count++;
        filledLabel.setText("📝 Filled: " + count + "/81");
    }

    // ============== ส่วน Solver (Backtracking) ==============

    // ตรวจสอบว่าเลข num ใส่ที่ตำแหน่ง (row, col) ได้หรือไม่
    // ตรวจ 3 เงื่อนไข: แถวเดียวกัน, คอลัมน์เดียวกัน, กล่อง 3x3 เดียวกัน
    private boolean isValid(int[][] board, int row, int col, int num) {
        // เช็คแถวและคอลัมน์พร้อมกัน (ลูปเดียว เพราะ Sudoku มี 9 แถว = 9 คอลัมน์)
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false; // ซ้ำ! → ใส่ไม่ได้
        }
        // เช็คกล่อง 3x3: หาจุดเริ่มต้นมุมซ้ายบนของกล่อง
        int sr = (row / SUBGRID) * SUBGRID, sc = (col / SUBGRID) * SUBGRID;
        for (int r = sr; r < sr + SUBGRID; r++)
            for (int c = sc; c < sc + SUBGRID; c++)
                if (board[r][c] == num)
                    return false; // ซ้ำในกล่อง! → ใส่ไม่ได้
        return true; // ผ่านทั้ง 3 เงื่อนไข → ใส่ได้ ✅
    }

    // === Solver ใช้ Backtracking ===
    // ใช้ Backtracking แก้โจทย์ที่ผู้ใช้กรอกมา (เติมเฉพาะช่องที่เป็น 0)
    private boolean solveBoard(int[][] board, int row, int col) {
        if (row == SIZE) // Base Case: แก้ครบทุกช่องแล้ว!
            return true;
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        // ถ้าช่องนี้มีเลขอยู่แล้ว (ผู้ใช้กรอกมา) → ข้ามไปช่องถัดไป
        if (board[row][col] != 0) {
            return solveBoard(board, nextRow, nextCol);
        }

        // ลองใส่เลข 1-9 ตามลำดับ (ไม่สุ่ม)
        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;
                if (solveBoard(board, nextRow, nextCol))
                    return true; // แก้ได้!
                board[row][col] = 0; // ⏪ Backtrack
            }
        }
        return false; // แก้ไม่ได้ → โจทย์ผิดหรือไม่มีคำตอบ
    }

    // ตรวจสอบว่า playerBoard ถูกกฎ Sudoku หรือไม่ (ไม่มีเลขซ้ำในแถว/คอลัมน์/กล่อง
    // 3x3)
    // ใช้ก่อน solve เพื่อจับ error เร็ว (ไม่ต้องรอ backtracking นาน)
    private boolean validateBoard() {
        for (int i = 0; i < SIZE; i++) {
            boolean[] rowCheck = new boolean[SIZE + 1]; // เช็คแถว
            boolean[] colCheck = new boolean[SIZE + 1]; // เช็คคอลัมน์
            for (int j = 0; j < SIZE; j++) {
                // เช็คแถว i
                int rv = playerBoard[i][j];
                if (rv != 0) {
                    if (rowCheck[rv])
                        return false; // ซ้ำในแถว!
                    rowCheck[rv] = true;
                }
                // เช็คคอลัมน์ i
                int cv = playerBoard[j][i];
                if (cv != 0) {
                    if (colCheck[cv])
                        return false; // ซ้ำในคอลัมน์!
                    colCheck[cv] = true;
                }
            }
        }
        // เช็คกล่อง 3x3 ทั้ง 9 กล่อง
        for (int br = 0; br < SIZE; br += SUBGRID) {
            for (int bc = 0; bc < SIZE; bc += SUBGRID) {
                boolean[] boxCheck = new boolean[SIZE + 1];
                for (int r = br; r < br + SUBGRID; r++) {
                    for (int c = bc; c < bc + SUBGRID; c++) {
                        int v = playerBoard[r][c];
                        if (v != 0) {
                            if (boxCheck[v])
                                return false; // ซ้ำในกล่อง!
                            boxCheck[v] = true;
                        }
                    }
                }
            }
        }
        return true; // ผ่าน ✅ ไม่มีเลขซ้ำ
    }

    // เช็คว่าช่อง (row, col) มีเลขซ้ำกับช่องอื่นในแถว/คอลัมน์/กล่อง 3x3 หรือไม่
    // ใช้เพื่อแจ้งเตือนทันทีขณะกรอก
    private boolean hasDuplicate(int row, int col) {
        int num = playerBoard[row][col];
        if (num == 0)
            return false; // ช่องว่างไม่มีทางซ้ำ

        // เช็คแถวและคอลัมน์
        for (int i = 0; i < SIZE; i++) {
            if (i != col && playerBoard[row][i] == num)
                return true; // ซ้ำในแถว
            if (i != row && playerBoard[i][col] == num)
                return true; // ซ้ำในคอลัมน์
        }
        // เช็คกล่อง 3x3
        int sr = (row / SUBGRID) * SUBGRID, sc = (col / SUBGRID) * SUBGRID;
        for (int r = sr; r < sr + SUBGRID; r++)
            for (int c = sc; c < sc + SUBGRID; c++)
                if (!(r == row && c == col) && playerBoard[r][c] == num)
                    return true; // ซ้ำในกล่อง
        return false; // ไม่ซ้ำ ✅
    }

    // เริ่มเกมโหมด Custom: กระดานเปล่า ผู้ใช้กรอกโจทย์เอง แล้วกด Solve เพื่อดูคำตอบ
    private void newCustomGame() {
        // ล้างกระดานทั้งหมด — ทุกช่องเป็น 0 (ว่าง) และแก้ไขได้
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                playerBoard[r][c] = 0;
                cells[r][c].setText("");
                cells[r][c].setForeground(TEXT_USER);
                cells[r][c].setEditable(true);
                cells[r][c].setBackground(BG_CELL);
            }
        }

        // รีเซ็ตสถานะ
        statusLabel.setText("Custom - Input puzzle, press Solve");
        statusLabel.setForeground(ACCENT);
        updateFilledCount(); // รีเซ็ตตัวนับช่อง
        selectedRow = -1;
        selectedCol = -1;
    }

    // เริ่มเกม Custom: สลับหน้าจอ (ถ้าจำเป็น) แล้วสร้างกระดานเปล่า
    private void startCustomGame() {
        // ให้สลับ Card ไปหน้า GAME
        cardLayout.show(mainContentPanel, "GAME");

        newCustomGame();
    }

    // เปิดเฉลย: ตรวจสอบโจทย์ก่อน แล้วแก้ด้วย Backtracking
    private void revealSolution() {
        // ถามยืนยันก่อนเปิดเฉลย
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reveal the solution?",
                "Reveal Solution", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            // ขั้นตอน 1: ตรวจว่าโจทย์ที่กรอก ถูกกฎ Sudoku หรือไม่ (ไม่มีเลขซ้ำ)
            if (!validateBoard()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid puzzle! Duplicate numbers found.\nPlease check your input.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Invalid Puzzle");
                statusLabel.setForeground(new Color(239, 68, 68));
                return;
            }

            // ขั้นตอน 2: แก้โจทย์ใน background thread (ไม่ให้ UI ค้าง)
            statusLabel.setText("Solving...");
            statusLabel.setForeground(ACCENT);
            // ล็อคทุกช่องระหว่างแก้
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++)
                    cells[r][c].setEditable(false);

            // จับเวลาเริ่มต้น
            long startTime = System.nanoTime();

            SwingWorker<boolean[], Void> worker = new SwingWorker<>() {
                int[][] boardToSolve = new int[SIZE][SIZE];
                long solveTimeUs; // เวลาที่ Solver ใช้ (ไมโครวินาที)

                @Override
                protected boolean[] doInBackground() {
                    // คัดลอก playerBoard ไปแก้
                    for (int r = 0; r < SIZE; r++)
                        System.arraycopy(playerBoard[r], 0, boardToSolve[r], 0, SIZE);
                    boolean solved = solveBoard(boardToSolve, 0, 0);
                    solveTimeUs = (System.nanoTime() - startTime) / 1_000; // แปลงเป็น ไมโครวินาที (µs)
                    return new boolean[] { solved };
                }

                @Override
                protected void done() {
                    try {
                        boolean solved = get()[0];
                        if (solved) {
                            // แก้ได้! → เก็บรายการช่องที่ต้องเติม แล้วแสดงแบบ Animation
                            java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
                            for (int r = 0; r < SIZE; r++) {
                                for (int c = 0; c < SIZE; c++) {
                                    cells[r][c].setEditable(false);
                                    if (playerBoard[r][c] == 0) {
                                        emptyCells.add(new int[] { r, c }); // เก็บช่องว่างไว้
                                    }
                                }
                            }

                            // แสดงเวลาที่ Solver ใช้ (แสดงเป็น ms พร้อมทศนิยม)
                            String timeStr = String.format("%.2f", solveTimeUs / 1000.0);
                            statusLabel.setText("✅ Solved in " + timeStr + "ms");
                            statusLabel.setForeground(ACCENT_GREEN);

                            // === Animation: แสดงคำตอบทีละช่อง (30ms ต่อช่อง) ===
                            if (!emptyCells.isEmpty()) {
                                Timer animTimer = new Timer(30, null);
                                final int[] index = { 0 }; // ตัวนับช่องที่แสดงแล้ว
                                animTimer.addActionListener(evt -> {
                                    if (index[0] < emptyCells.size()) {
                                        int[] pos = emptyCells.get(index[0]);
                                        int r = pos[0], c = pos[1];
                                        playerBoard[r][c] = boardToSolve[r][c];
                                        cells[r][c].setText(String.valueOf(boardToSolve[r][c]));
                                        cells[r][c].setForeground(ACCENT_GREEN);
                                        cells[r][c].setBackground(ACCENT_GREEN.darker().darker());
                                        // คืนสีหลังจาก 200ms
                                        Timer revert = new Timer(200, e2 -> highlightCells());
                                        revert.setRepeats(false);
                                        revert.start();
                                        index[0]++;
                                        updateFilledCount();
                                    } else {
                                        ((Timer) evt.getSource()).stop(); // หยุด animation
                                        highlightCells();
                                    }
                                });
                                animTimer.start();
                            }
                        } else {
                            // แก้ไม่ได้! → เปิดให้แก้ใหม่
                            for (int r = 0; r < SIZE; r++)
                                for (int c = 0; c < SIZE; c++)
                                    cells[r][c].setEditable(true);
                            JOptionPane.showMessageDialog(SudokuGame.this,
                                    "No solution exists for this puzzle!\nPlease check your input.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            statusLabel.setText("No Solution Found");
                            statusLabel.setForeground(new Color(239, 68, 68));
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        statusLabel.setText("Error");
                    }
                }
            };
            worker.execute();
        }
    }

    // === จุดเริ่มต้นของโปรแกรม ===
    public static void main(String[] args) {
        try {
            // ตั้งค่า UI ให้ดูเหมือนโปรแกรมบน OS (Windows/Mac)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ignored) {
        }

        // สร้างหน้าต่างเกมใน Event Dispatch Thread (กฎของ Swing)
        SwingUtilities.invokeLater(SudokuGame::new);
    }
}
