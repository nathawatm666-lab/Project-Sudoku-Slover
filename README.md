🧩 Modern Sudoku Solver & Game (Java Swing)แอปพลิเคชันเกมและโปรแกรมแก้ปริศนา Sudoku ที่เน้นดีไซน์ทันสมัย (Modern Dark UI) พร้อมระบบการแก้โจทย์อัตโนมัติด้วยอัลกอริทึมที่มีประสิทธิภาพ🌟 ฟีเจอร์เด่น (Features)Custom Puzzle Input: ผู้เล่นสามารถกรอกโจทย์ Sudoku ที่ต้องการลงบนกระดานได้เองSmart Highlighting: ระบบไฮไลท์อัจฉริยะ ช่วยให้มองเห็นแถว คอลัมน์ และกลุ่ม 3x3 ที่สัมพันธ์กับช่องที่เลือก รวมถึงแจ้งเตือนตัวเลขที่ซ้ำผิดกฎ (Error Highlight)Instant Solver: แก้โจทย์ได้ทันทีเพียงคลิกเดียว โดยใช้อัลกอริทึม BacktrackingAnimated Solution: แสดงขั้นตอนการเติมคำตอบทีละช่องด้วย Animation สวยงาม ไม่แสดงคำตอบทั้งหมดในทีเดียวModern UI: ดีไซน์แบบ Dark Mode โดยใช้ Java Swing พร้อมปุ่ม Custom Styled และข้อความ Title แบบคลื่น (Wave Animation)Performance Tracking: แสดงเวลาที่ใช้ในการคำนวณแก้โจทย์ในหน่วยมิลลิวินาที (ms)🛠 เทคนิคที่ใช้ (Technical Details)1. Algorithm: Backtrackingโปรแกรมใช้เทคนิค Recursion with Backtracking ในการค้นหาคำตอบ ซึ่งเป็นวิธีที่มีประสิทธิภาพสำหรับ Sudoku โดยจะลองวางตัวเลขและตรวจสอบความถูกต้อง หากไปต่อไม่ได้จะทำการย้อนกลับ (Backtrack) เพื่อลองตัวเลขใหม่$$\text{Time Complexity: } O(9^{n}) \quad (\text{โดยที่ } n \text{ คือจำนวนช่องว่าง})$$2. Multi-threading: SwingWorkerเพื่อไม่ให้หน้าจอโปรแกรมค้าง (Freezing) ขณะที่อัลกอริทึมกำลังประมวลผลแก้โจทย์ยากๆ โค้ดมีการใช้ SwingWorker เพื่อแยกการคำนวณไปไว้ใน Background Thread และส่งผลลัพธ์กลับมาอัปเดต UI ที่ Event Dispatch Thread (EDT)3. Graphics2D & Animationใช้ Graphics2D ในการวาดปุ่มและแอนิเมชันข้อความแบบกำหนดเองใช้ javax.swing.Timer ในการจัดการลำดับการแสดงผลแอนิเมชันการเติมตัวเลข🚀 วิธีการติดตั้งและรัน (Getting Started)ความต้องการของระบบ:Java Development Kit (JDK) เวอร์ชั่น 17 หรือสูงกว่าการรันโปรแกรม:Bashjavac SudokuGame.java
java SudokuGame
🎮 วิธีเล่น (How to Play)กดปุ่ม Start Game เพื่อเข้าสู่หน้ากระดานคลิกที่ช่องหรือใช้ ปุ่มลูกศร (↑ ↓ ← →) เพื่อเลื่อนตำแหน่งพิมพ์ตัวเลข 1-9 เพื่อกรอกโจทย์ หรือกด Backspace/Delete เพื่อลบหากต้องการดูเฉลย กดปุ่ม 👁 Solveหากต้องการล้างกระดานเพื่อเริ่มใหม่ กดปุ่ม 🗑 Clear All📸 ตัวอย่างโค้ดภายใน (Code Snippet)ตัวอย่างส่วนของอัลกอริทึมหลักที่ใช้ตรวจสอบความถูกต้อง:Javaprivate boolean isValid(int[][] board, int row, int col, int num) {
    for (int i = 0; i < 9; i++) {
        if (board[row][i] == num || board[i][col] == num) return false;
    }
    int sr = (row / 3) * 3, sc = (col / 3) * 3;
    for (int r = sr; r < sr + 3; r++)
        for (int c = sc; c < sc + 3; c++)
            if (board[r][c] == num) return false;
    return true;
}
Project by: [Your Name/Github Username]University: Naresuan University (CSIT)
