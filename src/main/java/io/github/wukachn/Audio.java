package io.github.wukachn;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Audio implements AutoCloseable, Runnable {

  private static final int SAMPLE_RATE = 16000;
  private static final double FREQUENCY = 1000.0;
  private SourceDataLine line;
  private byte soundTimer;

  public Audio() {
    AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    try {
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(audioFormat);
    } catch (LineUnavailableException e) {
      log.error("Couldn't initiate audio player.");
      throw new RuntimeException("Couldn't initiate audio player.", e);
    }
  }

  public void setTimer(byte val) {
    soundTimer = val;
  }

  public void decrementTimer() {
    if (soundTimer != 0) {
      soundTimer -= 1;
    }
  }

  private void playTone(SourceDataLine line) {
    byte[] buffer = new byte[1];
    for (int i = 0; i < SAMPLE_RATE / 2; i++) {
      double angle = 2.0 * Math.PI * i / (SAMPLE_RATE / Audio.FREQUENCY);
      buffer[0] = (byte) (Math.sin(angle) * 100);
      line.write(buffer, 0, 1);
    }
  }

  @Override
  public void run() {
    line.start();
    while (true) {
      if (soundTimer != 0) {
        playTone(line);
      }
    }
  }

  @Override
  public void close() throws Exception {
    line.drain();
    line.close();
  }
}
