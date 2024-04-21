package io.github.wukachn;

import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Memory {

  private static final int FONT_OFFSET = 80;
  private static final int PROGRAM_OFFSET = 512;
  private static final int MEMORY_SIZE = 4096;
  private static final byte[] FONT = {
      (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
      (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
      (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
      (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
      (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, // 4
      (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
      (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
      (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
      (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
      (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
      (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
      (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
      (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
      (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
      (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
      (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  // F
  };
  private static final byte[] MEMORY = new byte[MEMORY_SIZE];
  private int pc = PROGRAM_OFFSET;

  public Memory(String romPath) {
    log.info("Initializing Memory.");
    loadFont();
    loadProgram(romPath);
  }

  public short fetch() {
    short opcode = (short) (((MEMORY[pc] << 8) & 0xFF00) | ((MEMORY[pc + 1] & 0x00FF)));
    this.incrementPc();
    return opcode;
  }


  public byte[] getBytes(int address, int n) {
    byte[] subArray = new byte[n];
    System.arraycopy(MEMORY, address, subArray, 0, n);
    return subArray;
  }

  public int getPc() {
    return this.pc;
  }

  public void setPc(int newPc) {
    this.pc = newPc;
  }

  public void incrementPc() {
    this.pc += 2;
  }

  private void loadProgram(String romPath) {
    log.info("Loading Program.");
    try (FileInputStream fis = new FileInputStream(romPath)) {
      byte[] buffer = new byte[MEMORY_SIZE - PROGRAM_OFFSET];
      int bytesRead = fis.read(buffer);
      if (bytesRead == -1) {
        throw new RuntimeException("Loaded Empty Program.");
      }
      System.arraycopy(buffer, 0, MEMORY, PROGRAM_OFFSET, buffer.length);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load program.", e);
    }
  }

  private void loadFont() {
    log.info("Loading Font.");
    System.arraycopy(FONT, 0, MEMORY, FONT_OFFSET, FONT.length);
  }
}
