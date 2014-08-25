package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.gservice.GServiceMessages;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EndGameLayer extends Table {
  private Stage stage;
  private Image bgImg;
  private boolean visible = false;
  private TextButton bPlayAgain;
  private TextButton bLeaveMatch;
  private Label l1, l2;
  private Table pl0, pl1;
  private Group info;
  private Label waiting, abandoned, available;
  private boolean isWaiting = false;
  private boolean isAvailable = false;
  private int nPoints = 0;

  public EndGameLayer(Stage _stage) {
    stage = _stage;
    float width = stage.getWidth() / 1.3f;
    float height = stage.getHeight();

    abandoned = new Label("Opponent abandoned the match", GnuBackgammon.skin);
    waiting = new Label("Waiting for opponent response..", GnuBackgammon.skin);
    available = new Label("Opponent wants to play again..", GnuBackgammon.skin);
    waiting.addAction(MyActions.forever(MyActions.sequence(Actions.fadeOut(0.5f), Actions.fadeIn(0.4f))));
    waiting.setVisible(false);
    abandoned.setVisible(false);
    available.setVisible(false);

    info = new Group();
    info.addActor(waiting);
    info.addActor(abandoned);
    info.addActor(available);
    info.setWidth(waiting.getWidth());
    info.setHeight(waiting.getHeight());

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
    bPlayAgain = new TextButton("PLAY AGAIN", tl);
    bPlayAgain.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (bPlayAgain.isDisabled())
          return;
        GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_PLAY_AGAIN + " 1");
        if (isAvailable) {
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_RETURN_GAME, null);
          hide();
        } else
          waitForOpponent();
      }
    });
    bLeaveMatch = new TextButton("LEAVE MATCH", tl);
    bLeaveMatch.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (bLeaveMatch.isDisabled())
          return;
        GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_PLAY_AGAIN + " 0");
        GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
        hide();
      }
    });

    pl0 = new Table();
    pl0.setBackground(GnuBackgammon.skin.getDrawable("list"));

    pl1 = new Table();
    pl1.setBackground(GnuBackgammon.skin.getDrawable("list"));


    row().padTop(width / 20);
    add(new Label("GAME TERMINATED", GnuBackgammon.skin)).colspan(5);

    row();
    add().width(width / 9).expand();
    add().width(width / 3).expand();
    add().width(width / 9).expand();
    add().width(width / 3).expand();
    add().width(width / 9).expand();

    row().height(stage.getHeight() / 2);
    add();
    add(pl0).expand().fill();
    add(new Label("VS", GnuBackgammon.skin));
    add(pl1).expand().fill();
    add();

    row();
    add(info).colspan(5).expandY();

    row().height(height * 0.13f).padBottom(width / 20);;
    add();
    add(bLeaveMatch).fill().expandX();
    add();
    add(bPlayAgain).fill().expandX();
    add();

    setVisible(false);
    setY(stage.getHeight());
  }
  private void _updatePli(int winner) {
    pl0.clear();
    pl1.clear();
    String s0 = " ", s1 = " ";
    if (winner == 0)
      s0 = "WINNER! (" + nPoints + (nPoints > 1 ? "Pts)" : "Pt)");
    else
      s1 = "WINNER! (" + nPoints + (nPoints > 1 ? "Pts)" : "Pt)");

    Label _l0 = new Label(s0, GnuBackgammon.skin);
    Label _l1 = new Label(s1, GnuBackgammon.skin);

    pl0.setWidth(stage.getWidth());

    float imgDim = stage.getHeight() / 2 - _l0.getHeight() * 3.5f;


    pl0.add(_l0).expandX();
    pl0.row();
    Table t0 = new Table();
    t0.setBackground(GnuBackgammon.skin.getDrawable("border"));
    t0.add(GnuBackgammon.Instance.iconMe).height(imgDim).width(imgDim);
    pl0.add(t0);
    pl0.row();
    l1 = new Label("YOU", GnuBackgammon.skin);
    pl0.add(l1).expandX();


    pl1.setWidth(stage.getWidth());

    pl1.add(_l1).expandX();
    pl1.row();
    Table t1 = new Table();
    t1.setBackground(GnuBackgammon.skin.getDrawable("border"));
    t1.add(GnuBackgammon.Instance.iconOpponent).height(imgDim).width(imgDim);
    pl1.add(t1);
    pl1.row();
    l2 = new Label("OPPONENT", GnuBackgammon.skin);
    pl1.add(l2).expandX();

  }
  public void show(int _winner, int nPoints) {
    GnuBackgammon.Instance.nativeFunctions.showAds(false);
    this.nPoints = nPoints;
    _updatePli(_winner);
    l1.setText(GnuBackgammon.Instance.gameScreen.pInfo[1].getPName());
    l2.setText(GnuBackgammon.Instance.gameScreen.pInfo[0].getPName());
    bgImg.setX(GnuBackgammon.Instance.gameScreen.getBGX());
    visible = true;
    addAction(MyActions.moveTo(0, 0, 0.3f));
  }

  public void opponentAbandoned() {
    bPlayAgain.setColor(1, 1, 1, 0.4f);
    bPlayAgain.setDisabled(true);
    waiting.setVisible(false);
    available.setVisible(false);
    abandoned.setVisible(true);
  }

  public void opponentAvailable() {
    isAvailable = true;
    waiting.setVisible(false);
    abandoned.setVisible(false);
    available.setVisible(true);
  }

  public void waitForOpponent() {
    isWaiting = true;
    bPlayAgain.setColor(1, 1, 1, 0.4f);
    bPlayAgain.setDisabled(true);
    waiting.setVisible(true);
    abandoned.setVisible(false);
    available.setVisible(false);
  }

  public void hide() {
    GnuBackgammon.Instance.nativeFunctions.showAds(true);
    isAvailable = false;
    visible = false;
    isWaiting = false;
    nPoints = 0;
    setY(stage.getHeight());
    bPlayAgain.setDisabled(false);
    bPlayAgain.setColor(1, 1, 1, 1);
    waiting.setVisible(false);
    abandoned.setVisible(false);
    available.setVisible(false);
  }

  public int getPoints() {
    return nPoints;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean isWaiting() {
    return isWaiting;
  }

}
