package io.github.wukachn;

import java.io.Closeable;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Emulator implements Closeable {

  private static final int CPU_FREQ = 500;
  private static final int TIMER_FREQ = 60;
  private final Memory memory;
  private final Display display;
  private final Stack<Integer> stack;
  private final Keypad keypad;
  private final Audio audio;
  private final byte[] registers = new byte[16];
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private Thread audioThread;
  private short indexRegister;
  private byte delayTimer;

  public Emulator(String romPath) {
    this.memory = new Memory(romPath);
    this.stack = new Stack<>();
    this.keypad = new Keypad();
    this.display = new Display(keypad);
    this.audio = new Audio();
    audioThread = new Thread(audio);
    audioThread.start();
  }

  public void runProgram() {
    scheduler.scheduleAtFixedRate(() -> {
      short opcode = memory.fetch();
      decodeAndExecute(opcode);
    }, 0, 1000 / CPU_FREQ, TimeUnit.MILLISECONDS);
    scheduler.scheduleAtFixedRate(this::decrementTimers, 0, 1000 / TIMER_FREQ,
        TimeUnit.MILLISECONDS);
  }

  private void decrementTimers() {
    if (delayTimer != 0) {
      delayTimer -= 1;
    }
    audio.decrementTimer();
  }

  private void decodeAndExecute(short opcode) {
    switch (opcode & 0xF000) {
      case (0x0000):
        switch (opcode & 0x00FF) {
          case (0x00E0):
            handle00E0();
            break;
          case (0x00EE):
            handle00EE();
            break;
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
        switch (opcode & 0x000F) {
          case (0x0000):
            handle8XY0(opcode);
            break;
          case (0x0001):
            handle8XY1(opcode);
            break;
          case (0x0002):
            handle8XY2(opcode);
            break;
          case (0x0003):
            handle8XY3(opcode);
            break;
          case (0x0004):
            handle8XY4(opcode);
            break;
          case (0x0005):
            handle8XY5(opcode);
            break;
          case (0x0006):
            handle8XY6(opcode);
            break;
          case (0x0007):
            handle8XY7(opcode);
            break;
          case (0x000E):
            handle8XYE(opcode);
            break;
        }
        break;
      case (0x9000):
        handle9XY0(opcode);
        break;
      case (0xA000):
        handleANNN(opcode);
        break;
      case (0xB000):
        handleBNNN(opcode);
        break;
      case (0xC000):
        handleCXNN(opcode);
        break;
      case (0xD000):
        handleDXYN(opcode);
        break;
      case (0xE000):
        switch (opcode & 0x00FF) {
          case (0x009E):
            handleEX9E(opcode);
            break;
          case (0x00A1):
            handleEXA1(opcode);
            break;
        }
        break;
      case (0xF000):
        switch (opcode & 0x00FF) {
          case (0x0007):
            handleFX07(opcode);
            break;
          case (0x0015):
            handleFX15(opcode);
            break;
          case (0x0018):
            handleFX18(opcode);
            break;
          case (0x001E):
            handleFX1E(opcode);
            break;
          case (0x000A):
            handleFX0A(opcode);
            break;
          case (0x0029):
            handleFX29(opcode);
            break;
          case (0x0033):
            handleFX33(opcode);
            break;
          case (0x0055):
            handleFX55(opcode);
            break;
          case (0x0065):
            handleFX65(opcode);
            break;
        }
        break;
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
    short valX = registers[getX(opcode)];
    short nn = getNN(opcode);
    registers[getX(opcode)] = (byte) ((valX + nn) & 0xFF);
  }

  private void handle8XY0(short opcode) {
    registers[getX(opcode)] = registers[getY(opcode)];
  }

  private void handle8XY1(short opcode) {
    byte valX = registers[getX(opcode)];
    byte valY = registers[getY(opcode)];
    registers[getX(opcode)] = (byte) (valX | valY);
  }

  private void handle8XY2(short opcode) {
    byte valX = registers[getX(opcode)];
    byte valY = registers[getY(opcode)];
    registers[getX(opcode)] = (byte) (valX & valY);
  }

  private void handle8XY3(short opcode) {
    byte valX = registers[getX(opcode)];
    byte valY = registers[getY(opcode)];
    registers[getX(opcode)] = (byte) (valX ^ valY);
  }

  private void handle8XY4(short opcode) {
    short valX = (short) (registers[getX(opcode)] & 0xFF);
    short valY = (short) (registers[getY(opcode)] & 0xFF);
    short sum = (short) (valX + valY);
    registers[getX(opcode)] = (byte) (sum & 0xFF);
    if (sum > 255) {
      registers[15] = 0x01;
    } else {
      registers[15] = 0x00;
    }
  }

  private void handle8XY5(short opcode) {
    short valX = (short) (registers[getX(opcode)] & 0xFF);
    short valY = (short) (registers[getY(opcode)] & 0xFF);
    short result = (short) (valX - valY);
    registers[getX(opcode)] = (byte) (result & 0xFF);
    if (valX >= valY) {
      registers[15] = 0x01;
    } else {
      registers[15] = 0x00;
    }
  }

  private void handle8XY6(short opcode) {
    if (QuirkConfiguration.CPY_BEFORE_SHIFT) {
      registers[getX(opcode)] = registers[getY(opcode)];
    }
    byte currentVXValue = registers[getX(opcode)];
    byte shiftedBit = (byte) (currentVXValue & 0x01);
    registers[getX(opcode)] = (byte) (((currentVXValue & 0xFF) >> 1) & 0xFF);
    registers[15] = shiftedBit;
  }

  private void handle8XYE(short opcode) {
    if (QuirkConfiguration.CPY_BEFORE_SHIFT) {
      registers[getX(opcode)] = registers[getY(opcode)];
    }
    byte currentVXValue = registers[getX(opcode)];
    byte shiftedBit = (byte) ((currentVXValue >> 7) & 0x01);
    registers[getX(opcode)] = (byte) ((currentVXValue << 1) & 0xFF);
    registers[15] = shiftedBit;
  }

  private void handle8XY7(short opcode) {
    short valX = (short) (registers[getX(opcode)] & 0xFF);
    short valY = (short) (registers[getY(opcode)] & 0xFF);
    short result = (short) (valY - valX);
    registers[getX(opcode)] = (byte) (result & 0xFF);
    if (valY >= valX) {
      registers[15] = 0x01;
    } else {
      registers[15] = 0x00;
    }
  }

  private void handleANNN(short opcode) {
    this.indexRegister = getNNN(opcode);
  }

  private void handleBNNN(short opcode) {
    // TODO: Add quirk support - https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#annn-set-index:~:text=value%20NNN.-,BNNN,-%3A%20Jump%20with%20offset
    byte val0 = registers[0];
    memory.setPc(getNNN(opcode) + val0);
  }

  private void handleCXNN(short opcode) {
    byte valNN = getNN(opcode);
    Random random = new Random();
    char randomNumber = (char) random.nextInt(256);
    registers[getX(opcode)] = (byte) (randomNumber & valNN);
  }

  private void handleDXYN(short opcode) {
    int X =
        (registers[getX(opcode)] & 0xFF) % 64; //TODO: Extract width and height into vars instead
    int Y = (registers[getY(opcode)] & 0xFF) % 32;
    registers[15] = 0x00;
    int N = opcode & 0x000F;
    byte[] spriteBytes = memory.getBytes(indexRegister, N);
    boolean enableFlagRegister = display.paint(X, Y, spriteBytes);
    if (enableFlagRegister) {
      registers[15] = 0x01;
    }
  }

  private void handleEX9E(short opcode) {
    byte valX = registers[getX(opcode)];
    if (keypad.isKeyPressed(valX)) {
      memory.incrementPc();
    }
  }

  private void handleEXA1(short opcode) {
    byte valX = registers[getX(opcode)];
    if (!keypad.isKeyPressed(valX)) {
      memory.incrementPc();
    }
  }

  private void handleFX07(short opcode) {
    registers[getX(opcode)] = delayTimer;
  }

  private void handleFX15(short opcode) {
    delayTimer = registers[getX(opcode)];
  }

  private void handleFX18(short opcode) {
    audio.setTimer(registers[getX(opcode)]);
  }

  private void handleFX1E(short opcode) {
    byte valX = registers[getX(opcode)];
    indexRegister += valX;
  }

  private void handleFX0A(short opcode) {
    Optional<Character> optionalKey = keypad.getFirstPressedKey();
    if (optionalKey.isEmpty()) {
      memory.decrementPc();
    } else {
      char keyIndex = optionalKey.get();
      registers[getX(opcode)] = (byte) keyIndex;
    }
  }

  private void handleFX29(short opcode) {
    int valX = (registers[getX(opcode)] & 0xFF) * 6;
    indexRegister = (short) (Font.FONT_OFFSET + valX);
  }

  private void handleFX33(short opcode) {
    short valX = (short) (registers[getX(opcode)] & 0xFF);
    byte[] bytes = new byte[3];
    bytes[0] = (byte) (valX / 100);
    bytes[1] = (byte) ((valX % 100) / 10);
    bytes[2] = (byte) ((valX % 100) % 10);
    memory.setFromAddress(indexRegister, bytes);
  }

  private void handleFX55(short opcode) {
    int x = getX(opcode) + 0x01;
    byte[] bytes = new byte[x];
    System.arraycopy(registers, 0, bytes, 0, x);
    memory.setFromAddress(indexRegister, bytes);
    if (QuirkConfiguration.MEM_REG_CPY_INCREMENTS_I) {
      indexRegister += x;
    }
  }

  private void handleFX65(short opcode) {
    int x = getX(opcode) + 0x01;
    byte[] bytes = memory.getBytes(indexRegister, x);
    for (short i = 0; i < bytes.length; i++) {
      registers[i] = bytes[i];
    }
    if (QuirkConfiguration.MEM_REG_CPY_INCREMENTS_I) {
      indexRegister += x;
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

  @Override
  public void close() {
    scheduler.close();
  }
}
