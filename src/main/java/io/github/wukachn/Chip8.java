package io.github.wukachn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Chip8 {

  public static void main(String[] args) {
    if (args.length != 1) {
      log.error("Please provide a single argument for the ROM path.");
      return;
    }
    log.info("Starting Emulator.");
    String romPath = args[0];
    Memory m = new Memory(romPath);
  }
}