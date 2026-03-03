# 🧩 Modern Sudoku Solver & Game (Java Swing)

แอปพลิเคชันเกมและโปรแกรมแก้ปริศนา **Sudoku** ที่เน้นดีไซน์ทันสมัย (Modern Dark UI) พร้อมระบบการแก้โจทย์อัตโนมัติด้วยอัลกอริทึมที่มีประสิทธิภาพ พัฒนาโดยใช้ภาษา Java ทั้งหมด

---

## 🌟 ฟีเจอร์เด่น (Features)

* **Custom Puzzle Input:** ผู้เล่นสามารถกรอกโจทย์ Sudoku ที่ต้องการลงบนกระดานได้เองอย่างอิสระ
* **Smart Highlighting:** ระบบไฮไลท์อัจฉริยะ ช่วยให้มองเห็นแถว คอลัมน์ และกลุ่ม 3x3 ที่สัมพันธ์กับช่องที่เลือก
* **Error Detection:** แจ้งเตือนตัวเลขที่ซ้ำผิดกฎ Sudoku ทันทีด้วยสีแดง (Error Highlight)
* **Instant Solver:** แก้โจทย์ได้ทันทีเพียงคลิกเดียว โดยใช้เทคนิค **Backtracking Algorithm**
* **Animated Solution:** ระบบจะค่อยๆ เติมคำตอบให้เห็นทีละช่องด้วย Animation สวยงาม ไม่แสดงคำตอบทั้งหมดในทีเดียว
* **Modern UI & UX:** ดีไซน์แบบ Dark Mode, รองรับการใช้ปุ่มลูกศร (Arrow Keys) ในการเลื่อนช่อง และมี Title แบบคลื่น (Wave Animation)

---

## 🛠 เทคนิคทางด้านคอมพิวเตอร์ (Technical Details)

### 1. อัลกอริทึมการแก้โจทย์ (Backtracking)
โปรแกรมใช้เทคนิค **Recursion with Backtracking** ในการค้นหาคำตอบ ซึ่งเป็นหนึ่งในวิธีพื้นฐานของ Data Structures & Algorithms โดยจะลองวางตัวเลขและตรวจสอบความถูกต้อง หากไปต่อไม่ได้จะทำการย้อนกลับเพื่อลองตัวเลขใหม่


> **Complexity:** $$O(9^{n})$$ (โดยที่ $n$ คือจำนวนช่องว่าง)

### 2. การจัดการ Thread (SwingWorker)
เพื่อป้องกันอาการ "หน้าจอค้าง" (UI Freezing) ขณะที่อัลกอริทึมกำลังประมวลผลคำนวณโจทย์ที่ซับซ้อน โค้ดมีการใช้ `SwingWorker` เพื่อแยกส่วนการคำนวณไปไว้ใน **Background Thread** และส่งผลลัพธ์กลับมาแสดงผลที่หน้าจอผ่าน **Event Dispatch Thread (EDT)**

### 3. Java Swing Custom Graphics
* ใช้ `Graphics2D` ร่วมกับ `RenderingHints` เพื่อให้ตัวอักษรและปุ่มมีความคมชัด (Antialiasing)
* ใช้ `javax.swing.Timer` ในการจัดการลำดับการแสดงผลแอนิเมชันให้ดูนุ่มนวล

---

## 🚀 การติดตั้งและรันโปรแกรม (Getting Started)

1. **ความต้องการของระบบ:** Java Development Kit (JDK) 17 ขึ้นไป
2. **คำสั่งรันโปรแกรม:**
   ```bash
   javac SudokuGame.java
   java SudokuGame
