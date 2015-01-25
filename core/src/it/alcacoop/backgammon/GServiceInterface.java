package it.alcacoop.backgammon;

public interface GServiceInterface {
  public boolean gserviceIsSignedIn();
  public void gserviceSignIn();
  public void gserviceStartRoom();
  public void gserviceAcceptInvitation(String invitationId);
  public void gserviceSendReliableRealTimeMessage(String msg);
  public void gserviceResetRoom();
  public void gserviceOpenLeaderboards();
  public void gserviceOpenAchievements();
  public void gserviceSubmitRating(long score, String board_id);
  public void gserviceUpdateAchievement(String achievement_id, int increment);
  public void gserviceUnlockAchievement(String achiev_id);
  public void gserviceUpdateState();
  public void gserviceGetSigninDialog(int from);
  public void gserviceReset();
}
