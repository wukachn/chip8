package io.github.wukachn;

import java.awt.event.KeyEvent;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Keypad {

  private final boolean[] keys = new boolean[16];

  public Keypad() {
  }

  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case (KeyEvent.VK_1):
        keys[1] = true;
        break;
      case (KeyEvent.VK_2):
        keys[2] = true;
        break;
      case (KeyEvent.VK_3):
        keys[3] = true;
        break;
      case (KeyEvent.VK_4):
        keys[12] = true;
        break;
      case (KeyEvent.VK_Q):
        keys[4] = true;
        break;
      case (KeyEvent.VK_W):
        keys[5] = true;
        break;
      case (KeyEvent.VK_E):
        keys[6] = true;
        break;
      case (KeyEvent.VK_R):
        keys[13] = true;
        break;
      case (KeyEvent.VK_A):
        keys[7] = true;
        break;
      case (KeyEvent.VK_S):
        keys[8] = true;
        break;
      case (KeyEvent.VK_D):
        keys[9] = true;
        break;
      case (KeyEvent.VK_F):
        keys[14] = true;
        break;
      case (KeyEvent.VK_Z):
        keys[10] = true;
        break;
      case (KeyEvent.VK_X):
        keys[0] = true;
        break;
      case (KeyEvent.VK_C):
        keys[11] = true;
        break;
      case (KeyEvent.VK_V):
        keys[15] = true;
        break;
    }
  }

  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case (KeyEvent.VK_1):
        keys[1] = false;
        break;
      case (KeyEvent.VK_2):
        keys[2] = false;
        break;
      case (KeyEvent.VK_3):
        keys[3] = false;
        break;
      case (KeyEvent.VK_4):
        keys[12] = false;
        break;
      case (KeyEvent.VK_Q):
        keys[4] = false;
        break;
      case (KeyEvent.VK_W):
        keys[5] = false;
        break;
      case (KeyEvent.VK_E):
        keys[6] = false;
        break;
      case (KeyEvent.VK_R):
        keys[13] = false;
        break;
      case (KeyEvent.VK_A):
        keys[7] = false;
        break;
      case (KeyEvent.VK_S):
        keys[8] = false;
        break;
      case (KeyEvent.VK_D):
        keys[9] = false;
        break;
      case (KeyEvent.VK_F):
        keys[14] = false;
        break;
      case (KeyEvent.VK_Z):
        keys[10] = false;
        break;
      case (KeyEvent.VK_X):
        keys[0] = false;
        break;
      case (KeyEvent.VK_C):
        keys[11] = false;
        break;
      case (KeyEvent.VK_V):
        keys[15] = false;
        break;
    }
  }

  public boolean isKeyPressed(short keyIndex) {
    return keys[keyIndex];
  }

  public Optional<Character> getFirstPressedKey() {
    for (char i = 0; i < 16; i++) {
      if (keys[i]) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }
}
