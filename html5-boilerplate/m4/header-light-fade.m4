`
<div id="light" class="light">
	<div class="page-login">
	    <div class="close-lb">
		    <a href=""><img id="login-close-box" src="/img/close.png"></a>
	    </div>
	    <div class="login-panel">
		    <div class="header-content-login">
			    <span>@lang_login_header@</span>
		    </div>
		    <div id="login-panel-text" class="login-panel-text">@lang_login_detail@</div>
		    <div class="login-box">
			    <div class="login-panel-left">
				    <form id="register-form" method="POST" action="/user/register.html">
				        <input id="register-email" name="email" type="text" value="@lang_login_username@"></input><br>
				        <input id="register-password" name="password" type="password" value=""></input><br>
				        <a href="#" id="signin-link" class="zaloguj-button signin-button">@lang_signin@</a>
				        <a href="#" id="register-link" class="zaloguj-button register-button">@lang_new_user@</a>
				        <div id="register-message" class="register-massage attention"></div>
                    </form>
    		        <div id="register-verify-text" class="login-panel-text initialhidden">@lang_login_verify_message@</div>
				    <a id="register-close-link" href="#"  class="zaloguj-button register-close initialhidden">@lang_close@</a>
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
