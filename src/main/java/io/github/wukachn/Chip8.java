package io.github.wukachn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Chip8 {

  public static void main(String[] args) {
    if (args.length != 1) {
      log.error("Please provide a single argument for the ROM path.");
      return;
    }

    log.info("Creating Emulator.");
    String romPath = args[0];
    Emulator emulator = new Emulator(romPath);

    log.info("Starting Emulator.");
    emulator.runProgram();
  }
}