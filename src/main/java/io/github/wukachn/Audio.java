package io.github.wukachn;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Audio implements AutoCloseable {

  private static final int SAMPLE_RATE = 44000;
  private static final double FREQUENCY = 500.0;
  private final SourceDataLine line;
  private byte soundTimer;

  public Audio() {
    log.info("Initializing Audio.");
    final AudioFormat audioFormat = new AudioFormat((float) SAMPLE_RATE, 8, 1, true, true);
    try {
      this.line = AudioSystem.getSourceDataLine(audioFormat);
      this.line.open(audioFormat);
    } catch (LineUnavailableException e) {
      log.error("Couldn't initiate audio player.");
      throw new RuntimeException("Couldn't initiate audio player.", e);
    }
    this.line.start();
  }

  public void setTimer(byte val) {
    soundTimer = val;
  }

  public void decrementTimer() {
    if (soundTimer != 0) {
      soundTimer -= 1;
      if (soundTimer > 0) {
        playBeep();
      }
    }
  }

  private void playBeep() {
    int numSamples = ((1000 / 60) * SAMPLE_RATE) / 1000;
    byte[] buffer = new byte[numSamples];
    for (int i = 0; i < numSamples; i++) {
      double angle = 2.0 * Math.PI * FREQUENCY * i / SAMPLE_RATE;
      buffer[i] = (byte) (Math.sin(angle) * 127);
    }
    line.write(buffer, 0, buffer.length);
  }

  @Override
  public void close() throws Exception {
    line.close();
  }
}
