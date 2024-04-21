package io.github.wukachn;

import java.io.FileInputStream;
import java.io.IOException;

public class Memory {

  private static int PROGRAM_OFFSET = 512;
  private static int MEMORY_SIZE = 4096;

  byte[] MEMORY = new byte[MEMORY_SIZE];

  public Memory(String romPath) {
    loadProgram(romPath);
  }

  private void loadProgram(String romPath) {
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

}
