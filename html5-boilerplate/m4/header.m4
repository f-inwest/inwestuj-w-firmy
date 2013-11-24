`
<div class="header" id="header">
  <div class="container headerrow" style="width: 960px;">
    <div class="span-6 headerrow hoverlink">
        <a href="discover-page.html">
            <img class="headerlogoimg" src="img/logo.png"/>
        </a>
    </div>
    <div class="span-8 headerrow headerrowcenter last">
      <a href="discover-page.html"><span class="headerlink hoverlink headerlinknoborder">@lang_find@</span></a>
      <a href="nearby-page.html"><span class="headerlink"><span class="hoverlink">@lang_nearby@</span></span></a>
      <a href="add-listing-page.html"><span class="headerlink"><span class="hoverlink">@lang_add@</span></span></a>
    </div>

    <div class="span-6 headerrow headersearchrow">
        <form id="searchform" action="/main-page.html">
            <input type="hidden" name="type" value="keyword"></input>
            <input type="image" class="searchbutton" alt="search" width="200px" height="42px" title="" src="/img/lupa.png" ></input>
            <input type="text" class="text inputtext searchtext" name="searchtext" id="searchtext" value=""></input>
        </form>
    </div>

    <!-- not logged in -->
    <div class="span-5 last loginspan headerrow initialhidden" id="headernotloggedin">
        <div>
            <a href="/login-page.html">
                <span class="headerlink headerlinkright">@lang_signin@</span>
            </a>
            <a id="loginlink" href="">
                <div class="headericon headersignin"></div>
            </a>
            <a id="twitter_loginlink" href="">
                <div class="headericon headertwittersignin"></div>
            </a>
            <a id="fb_loginlink" href="">
                <div class="headericon headerfbsignin"></div>
            </a>
        </div>
    </div>

    <!-- logged in -->
    <div class="span-5 last loginspan headerrow initialhidden headerloggedin" id="headerloggedin">
        <div>
            <a id="logoutlink" href="">
                <div class="headericon headerlogout" title="Logout"></div>
            </a>
            <a href="/notifications-page.html">
                <div class="headericon headernotifications" title="Notifications">
                    <div class="headernum" id="headernumnotifications"></div>
                </div>
            </a>
            <a href="/message-group-page.html">
                <div class="headericon headermessages" title="Messages">
                    <div class="headernum" id="headernummessages"></div>
                </div>
            </a>
            <a href="/profile-page.html">
                <div title="View Your Profile" class="profileavatar headeravatar" id="headeravatar"></div>
            </a>
        </div>
    </div>

  </div>
</div>
'
