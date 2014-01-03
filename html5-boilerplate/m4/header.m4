`
<div class="header" id="header">
  <div class="container headerrow" style="width: 960px;">
    <div class="span-6 headerrow hoverlink">
        <a href="discover-page.html">
            <img class="headerlogoimg" src="img/logo.png"/>
        </a>
    </div>
    <div class="span-7 headerrow headerrowcenter last">
      <a href="discover-page.html"><span class="headerlink hoverlink headerlinknoborder">@lang_find@</span></a>
      <a href="nearby-page.html"><span class="headerlink"><span class="hoverlink">@lang_nearby@</span></span></a>
      <a href="add-listing-page.html"><span class="headerlink"><span class="hoverlink">@lang_add@</span></span></a>
    </div>
    <a href="#" id="headerloginlink">
    <div class="span-2 headerrow headerrowlogin last initialhidden" id="headernotloggedin">
      <span class="headerrowlogintext"><span class="hoverlink">@lang_signin@</span></span>
    </div>
    </a>

    <div class="span-6 headerrow headersearchrow">
        <form id="searchform" action="/main-page.html">
            <input type="hidden" name="type" value="keyword"></input>
            <input type="image" class="searchbutton" alt="search" width="200px" height="42px" title="" src="/img/lupa.png" ></input>
            <input type="text" class="text inputtext searchtext" name="searchtext" id="searchtext" value=""></input>
        </form>
    </div>

    <!-- logged in -->
    <div class="span-6 last loginspan headerrow initialhidden" id="headerloggedin">
        <div class="headericonset">
            <a id="logoutlink" href="">
                <div class="headericonbar headerlogout button-content" title="Logout"></div>
            </a>
            <a href="/notifications-page.html">
                <div class="headericonbar headernotifications button-content" title="Notifications">
                    <div class="headernum" id="headernumnotifications"></div>
                </div>
            </a>
            <a href="/message-group-page.html">
                <div class="headericonbar headermessages button-content" title="Messages">
                    <div class="headernum" id="headernummessages"></div>
                </div>
            </a>
            <a href="/profile-page.html">
                <div title="View Your Profile" class="headeravatar button-content" id="headeravatar"></div>
            </a>
        </div>
    </div>

  </div>
</div>

<div id="light" class="light">
	<div class="page-login">
	    <div class="close-lb">
		    <a href=""><img id="login-close-box" src="/img/close.png"></a>
	    </div>
	    <div class="login-panel">
		    <div class="header-content-login">
			    <span>@lang_login_header@</span>
		    </div>
		    <div class="login-panel-text">@lang_login_detail@</div>
		    <div class="login-box">
			    <div>
				    <input id="login" value="@lang_login_username@"><br>
				    <input id="passw" value="@lang_login_password@"><br>
				    <a href="#" class="zaloguj-button">@lang_signin@</a>
			    </div>
			    <div id="social-login">
				    <div class="headericon headersignin">
					    <a class="social-loginlink" id="loginlink" href="#">@lang_login_google@</a>
				    </div>
                    <div class="headericon headertwittersignin">
					    <a class="social-loginlink" id="twitter_loginlink" href="#">@lang_login_twitter@</a>
                    </div>
		            <div class="headericon headerfbsignin">
					    <a class="social-loginlink" id="fb_loginlink" href="#">@lang_login_facebook@</a>
				    </div>
			    </div>
		    </div>
	    </div>
	</div>
</div>
<div id="fade" class="fade"></div>
'
