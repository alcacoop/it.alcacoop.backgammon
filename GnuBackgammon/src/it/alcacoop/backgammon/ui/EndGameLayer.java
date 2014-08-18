package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.GameFSM;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EndGameLayer extends Table {
  private Stage stage;
  private Image bgImg;
  private boolean visible = false;
  private TextButton bPlayAgain;
  private TextButton bLeaveMatch;
  private Label l1, l2;
  private int winner = -1;

  public EndGameLayer(Stage _stage) {
    stage = _stage;
    int winner = 0;
    float width = stage.getWidth() / 1.3f;
    float height = stage.getHeight();

    setWidth(stage.getWidth());
    setHeight(stage.getHeight());

    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    bgImg.setHeight(stage.getHeight());
    bgImg.setWidth(stage.getWidth() * 1.2f);
    addActor(bgImg);
    NinePatch patch = null;
    TextureRegion r = GnuBackgammon.atlas.findRegion("alca");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);

    Image alca = new Image(patch);
    alca.setWidth(stage.getWidth());
    alca.setPosition(0, 0);
    addActor(alca);

    NinePatch patch2 = null;
    TextureRegion r2 = GnuBackgammon.atlas.findRegion("topborder");
    splits = ((AtlasRegion)r2).splits;
    patch2 = new NinePatch(r2, splits[0], splits[1], splits[2], splits[3]);

    Image top = new Image(patch2);
    top.setWidth(stage.getWidth());
    top.setPosition(0, stage.getHeight() - top.getHeight());
    addActor(top);


    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    bPlayAgain = new TextButton("Play Again", tl);
    bPlayAgain.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.GSERVICE_RETURN_GAME, EndGameLayer.this.winner);
      }
    });
    bLeaveMatch = new TextButton("Leave Match", tl);
    bLeaveMatch.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide();
      }
    });


    row().padTop(width / 20);
    add(new Label("GAME TERMINATED", GnuBackgammon.skin)).colspan(5);

    row();
    add().colspan(5).expand().fill();

    String s0 = "", s1 = "";
    if (winner == 0)
      s0 = "WINNER!";
    else
      s1 = "WINNER!";


    Table pl0 = new Table();
    pl0.setBackground(GnuBackgammon.skin.getDrawable("list"));
    pl0.add().expand().fill();
    pl0.row();
    pl0.add(new Label(s0, GnuBackgammon.skin)).expand();
    pl0.row();
    Table t0 = new Table();
    t0.setBackground(GnuBackgammon.skin.getDrawable("border"));
    t0.add(GnuBackgammon.Instance.iconMe);
    pl0.add(t0);
    pl0.row();
    l1 = new Label("YOU", GnuBackgammon.skin);
    pl0.add(l1).align(Align.center).center();
    pl0.row();
    pl0.add().expand().fill();


    Table pl1 = new Table();
    pl1.setBackground(GnuBackgammon.skin.getDrawable("list"));
    pl1.add().expand().fill();
    pl1.row();
    pl1.add(new Label(s1, GnuBackgammon.skin)).expand();
    pl1.row();
    Table t1 = new Table();
    t1.setBackground(GnuBackgammon.skin.getDrawable("border"));
    t1.add(GnuBackgammon.Instance.iconOpponent);
    pl1.add(t1);
    pl1.row();
    l2 = new Label("OPPONENT", GnuBackgammon.skin);
    pl1.add(l2).align(Align.center).center();
    pl1.row();
    pl1.add().expand().fill();

    row();
    add();
    add(pl0).width(width / 3.5f).fill().expand();
    add(new Label("VS", GnuBackgammon.skin)).fill();
    add(pl1).fill().width(width / 3.5f).fill().expand();
    add();

    row();
    add().colspan(5).expand().fill();

    row().padBottom(width / 20);
    add();
    add(bLeaveMatch).width(width / 3).height(height * 0.13f);
    add();
    add(bPlayAgain).fill().width(width / 3).height(height * 0.13f);
    add();

    setVisible(false);
    setY(stage.getHeight());
  }

  public void show(int _winner) {
    winner = _winner;
    l1.setText(GnuBackgammon.Instance.gameScreen.pInfo[0].getPName());
    l2.setText(GnuBackgammon.Instance.gameScreen.pInfo[1].getPName());
    bgImg.setX(GnuBackgammon.Instance.gameScreen.getBGX());
    visible = true;
    addAction(MyActions.moveTo(0, 0, 0.3f));
  }

  public void hide() {
    visible = false;
    winner = -1;
    setY(stage.getHeight());
  }

  public boolean isVisible() {
    return visible;
  }
}
