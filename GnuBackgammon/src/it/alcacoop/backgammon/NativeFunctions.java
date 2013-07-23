/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon;

import it.alcacoop.backgammon.utils.MatchRecorder;

public interface NativeFunctions {
  public void showAds(boolean show);
  public void openURL(String url);
  public void openURL(String url, String fallback);
  public String getDataDir(); 
  public void shareMatch(MatchRecorder rec);
  public void injectBGInstance();
  
  public void fibsSignin();
  public void fibsRegistration();
  public boolean isNetworkUp();
  public void hideChatBox();
  public void showChatBox();
  public void showInterstitial();
  public void initEngine();
  
  public boolean isProVersion();
  public void inAppBilling();
  
  public void hideProgressDialog();
  
  public void gserviceSignIn();
  public boolean gserviceIsSignedIn();
  public void gsericeStartRoom();
  public void gserviceAcceptInvitation(String invitationId);
  public void gserviceSendReliableRealTimeMessage(String msg);
  public void gserviceResetRoom();
  public void gserviceStopPing();
  public void gserviceOpenLeaderboards();
  public void gserviceOpenAchievements();
}
