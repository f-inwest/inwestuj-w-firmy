`
  <div class="container headerrow" style="width: 960px;">
    <div class="span-6 headerrow hoverlink">
        <a href="discover-page.html">
            <img class="headerlogoimg" src="img/logo.png"/>
        </a>
    </div>
    <div class="span-7 headerrow headerrowcenter last">
      <a href="discover-page.html"><span class="headerlink hoverlink headerlinknoborder">@lang_find@</span></a>
      <a href="nearby-page.html"><span class="headerlink"><span class="hoverlink">@lang_nearby@</span></span></a>
      <a href="#" id="headeraddlistinglink"><span class="headerlink"><span class="hoverlink">@lang_add@</span></span></a>
    </div>
    <a href="#" id="headerloginlink">
    <div class="span-2 headerrow headerrowlogin last" id="headernotloggedin">
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
                <div class="headericonbar headerlogout button-content" title="@lang_logout@"></div>
            </a>
            <a href="/notifications-page.html">
                <div class="headericonbar headernotifications button-content" title="@lang_notifications@">
                    <div class="headernum" id="headernumnotifications"></div>
                </div>
            </a>
            <a href="/message-group-page.html">
                <div class="headericonbar headermessages button-content" title="@lang_messages@">
                    <div class="headernum" id="headernummessages"></div>
                </div>
            </a>
            <a href="/profile-page.html">
                <div title="@lang_view_your_profile@" class="headeravatar button-content" id="headeravatar"></div>
            </a>
        </div>
    </div>

  </div>

  <div class="campaign-selector-wrapper">
    <div class="campaign-selector">
      <div class="campaign-dropdown-button" id="campaign-dropdown-button"></div>
      <div class="campaign-dropdown" id="campaign-dropdown">
          <div class="campaign-dropdown-help">@lang_campaign_dropdown_help@</div>
          <div class="campaign-textbox" id="campaign-textbox">@lang_select_campaign@</div>
          <div class="campaign-textbox-additional" id="campaign-textbox-additional"></div>
      </div>
    </div>
  </div>

'
