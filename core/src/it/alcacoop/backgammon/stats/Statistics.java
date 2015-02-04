package it.alcacoop.backgammon.stats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.utils.Base64Coder;


public class Statistics implements Serializable {
  private static final long serialVersionUID = -4403320414647151275L;

  public Dices dices;
  public General general;

  public class Dices implements Serializable {
    // FOR STAT INFO: http://www.gammonsite.com/dice2.asp
    private static final long serialVersionUID = -5564345208265926744L;

    public byte[][] DOUBLE_HISTORY = {
        { 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0 }
    };

    public int[] ROLLS = { 0, 0 };
    public int[] DOUBLES = { 0, 0 };
    public float[] AVG_PIPS = { 0.0f, 0.0f };

    public int[] BAR_ENTER_ATTEMPT = { 0, 0 };
    public int[] BAR_ENTER = { 0, 0 };

    public int[] DOUBLES_ROW_1 = { 0, 0 };
    public int[] DOUBLES_ROW_2 = { 0, 0 };
    public int[] DOUBLES_ROW_3 = { 0, 0 };
    public int[] DOUBLES_ROW_4 = { 0, 0 };

    public int[] BAR_ENTER_ATTEMPT_P1 = { 0, 0 };
    public int[] BAR_ENTER_P1 = { 0, 0 };
    public int[] BAR_ENTER_ATTEMPT_P2 = { 0, 0 };
    public int[] BAR_ENTER_P2 = { 0, 0 };
    public int[] BAR_ENTER_ATTEMPT_P3 = { 0, 0 };
    public int[] BAR_ENTER_P3 = { 0, 0 };
    public int[] BAR_ENTER_ATTEMPT_P4 = { 0, 0 };
    public int[] BAR_ENTER_P4 = { 0, 0 };
    public int[] BAR_ENTER_ATTEMPT_P5 = { 0, 0 };
    public int[] BAR_ENTER_P5 = { 0, 0 };
  }

  public class General implements Serializable {
    private static final long serialVersionUID = 8639932045227909953L;
    public int HUMAN = 0;
    public int CPU = 0;
  }


  public Statistics() {
    general = new General();
    dices = new Dices();
  }

  public String serialize() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    String ret = "";
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      oos.close();
      ret = new String(Base64Coder.encode(baos.toByteArray()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  public static Statistics deserialize(String s) throws Exception {
    byte[] data = Base64Coder.decode(s);
    ObjectInputStream ois;
    ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return (Statistics)o;
  }
}
