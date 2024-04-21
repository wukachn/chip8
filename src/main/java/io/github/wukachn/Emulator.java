package io.github.wukachn;

import java.util.Stack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Emulator {

  private final Memory memory;
  private final Display display;
  private final Stack<Integer> stack;
  private final byte[] registers = new byte[16];
  private short indexRegister;

  public Emulator(String romPath) {
    this.memory = new Memory(romPath);
    this.stack = new Stack<>();
    this.display = new Display();
  }

  public void runProgram() {
    while (true) {
      short opcode = memory.fetch();
      switch (opcode & 0xF000) {
        case (0x0000):
          if (opcode == 0x00E0) {
            handle00E0();
          } else if (opcode == 0x00EE) {
            handle00EE();
          }
          break;
        case (0x1000):
          handle1NNN(opcode);
          break;
        case (0x2000):
          handle2NNN(opcode);
          break;
        case (0x3000):
          handle3XNN(opcode);
          break;
        case (0x4000):
          handle4XNN(opcode);
          break;
        case (0x5000):
          handle5XY0(opcode);
          break;
        case (0x6000):
          handle6XNN(opcode);
          break;
        case (0x7000):
          handle7XNN(opcode);
          break;
        case (0x8000):
          break;
        case (0x9000):
          handle9XY0(opcode);
          break;
        case (0xA000):
          handleANNN(opcode);
          break;
        case (0xB000):
          break;
        case (0xC000):
          break;
        case (0xD000):
          handleDXYN(opcode);
          break;
        case (0xE000):
          break;
        case (0xF000):
          break;
      }
    }
  }

  private void handle00E0() {
    display.clear();
  }

  private void handle00EE() {
    int pc = stack.pop();
    memory.setPc(pc);
  }

  private void handle1NNN(short opcode) {
    memory.setPc(getNNN(opcode));
  }

  private void handle2NNN(short opcode) {
    stack.push(memory.getPc());
    memory.setPc(getNNN(opcode));
  }

  private void handle3XNN(short opcode) {
    byte val = registers[getX(opcode)];
    if (val == getNN(opcode)) {
      memory.incrementPc();
    }
  }

  private void handle4XNN(short opcode) {
    byte val = registers[getX(opcode)];
    if (val != getNN(opcode)) {
      memory.incrementPc();
    }
  }

  private void handle5XY0(short opcode) {
    byte valX = registers[getX(opcode)];
    byte valY = registers[getY(opcode)];
    if (valX == valY) {
      memory.incrementPc();
    }
  }

  private void handle9XY0(short opcode) {
    byte valX = registers[getX(opcode)];
    byte valY = registers[getY(opcode)];
    if (valX != valY) {
      memory.incrementPc();
    }
  }

  private void handle6XNN(short opcode) {
    registers[getX(opcode)] = getNN(opcode);
  }

  private void handle7XNN(short opcode) {
    registers[getX(opcode)] += getNN(opcode);
  }

  private void handleANNN(short opcode) {
    this.indexRegister = getNNN(opcode);
  }

  private void handleDXYN(short opcode) {
    int X = registers[getX(opcode)] % 64; //TODO: Extract width and height into vars instead
    int Y = registers[getY(opcode)] % 32;
    registers[15] = 0x00;
    int N = opcode & 0x000F;
    byte[] spriteBytes = memory.getBytes(indexRegister, N);
    boolean enableFlagRegister = display.paint(X, Y, spriteBytes);
    if (enableFlagRegister) {
      registers[15] = 0x01;
    }
  }

  private short getNNN(short opcode) {
    return (short) (opcode & 0x0FFF);
  }

  private byte getNN(short opcode) {
    return (byte) (opcode & 0x00FF);
  }

  private byte getX(short opcode) {
    return (byte) ((opcode >> 8) & 0x0F);
  }

  private byte getY(short opcode) {
    return (byte) ((opcode >> 4) & 0x00F);
  }
}
