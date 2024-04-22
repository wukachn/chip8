package io.github.wukachn;

import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Memory {

  private static final int PROGRAM_OFFSET = 512;
  private static final int MEMORY_SIZE = 4096;
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

  public void setFromAddress(int address, byte[] bytes) {
    for (short i = 0; i < bytes.length; i++) {
      MEMORY[address + i] = bytes[i];
    }
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

  public void decrementPc() {
    this.pc -= 2;
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
    System.arraycopy(Font.FONT, 0, MEMORY, Font.FONT_OFFSET, Font.FONT.length);
  }
}
