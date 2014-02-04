`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="edit-profile-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
include(profile-not-found.m4)
`
<div class="container">
    <div class="bannersmall" style="float:left;">
        <span class="bannertextsmall span-16">@lang_editing_profile@</span>
    </div> <!-- end banner -->

    <div>
        <a href="profile-page.html" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                &lt;&lt; @lang_back@
            </span>
        </a>
    </div>
</div>

<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="container">

<!-- left column -->
<div class="span-24 initialhidden" id="personalcolumn">
    <div class="boxtitle">
        @lang_personal_info@
        <span class="newlistingtitlemsg" id="personalinfomsg"></span>
    </div>

    <div class="boxpanel boxpanelfull editprofilepanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="email">@lang_email@</label>
            <span class="inputfield">
                <div class="inputhelp inputmsg" id="email"></div>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_email@</label>
                <br />
                Your private email address on inwestuj-w-firmy, never shown on the site, only used to notify you for personal communications.
            </p>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="username">@lang_nickname@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="username" id="username" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_nickname@</label>
                <br />
                Your publicly visible username on inwestuj-w-firmy.
            </p>
            <span class="inputicon">
                <div id="usernameicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="name">@lang_name@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="name" id="name" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_name@</label>
                <br />
                Your full legal name, kept private, only shown upon successful conclusion of a private bid and only shown to the bid counterparty, never made public.
            </p>
            <span class="inputicon">
                <div id="nameicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="notify_enabled">@lang_notify@</label>
            <span class="inputcheckbox">
                <div id="notify_enabled"></div>
            </span>
            <span class="inputhelp">Receive a copy of each notification via email</span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_notify@</label>
                <br />
                Send a copy of each notification to your email address, otherwise you must login to inwestuj-w-firmy to check notifications.
            </p>
            <span class="inputicon">
                <div id="notify_enabledicon"></div>
            </span>
        </div>

<!--
        <div class="formitem clear">
            <span class="inputlabel">INVESTOR</span>
            <span class="inputcheckbox">
                <div id="investor"></div>
            </span>
            <span class="inputhelp">You are an accredited investor in your jurisdiction</span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">TITLE</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">COMPANY</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="organization" id="organization" value=""></input>
            </span>
            <span class="inputicon">
                <div id="organizationicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">PHONE</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="phone" id="phone" value=""></input>
            </span>
            <span class="inputicon">
                <div id="phoneicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">LOCATION</span>
            <span class="inputfield">
                <input class="text inputwidetext error" type="text" name="address" id="address" value=""></input>
            </span>
            <span class="inputicon">
                <div id="addressicon"></div>
            </span>
        </div>
-->
    </div>
<!--
    <div class="boxtitle">SETTINGS</div>
    <div class="boxpanel">
        <div class="formitem clear">
            <span class="inputlabel">INVESTOR</span>
            <span class="inputcheckbox">
                <div class="investor"></div>
            </span>
            <span class="inputhelp">Accredited investor in your jurisdiction</span>
        </div>
    </div>

    <div class="boxtitle">CHANGE PASSWORD</div>
    <div class="boxpanel">
        <div class="formitem clear">
            <span class="inputlabel">NEW</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="password" name="newpassword" id="newpassword"></input>
            </span>
            <span class="inputicon">
                <div id="newpasswordicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">CONFIRM</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="password" name="confirmpassword" id="confirmpassword" disabled="disabled"></input>
            </span>
            <span class="inputicon">
                <div id="confirmpasswordicon"></div>
            </span>
        </div>
        <div class="formitem clear">
	        <span class="inputlabel"></span>
            <span class="span-12 inputhelp inputmsg" id="passwordmsg"></span>
        </div>
    </div>
-->

    <div id="deactivatebox" class="indentedtextbox deactivateguardbox">
        <a href="#" id="deactivateguardlink">@lang_deactivate_account@</a>
        <div class="initialhidden deactivatebox" id="deactivateguard">
            <p>@lang_deactivate_warning@</p>
            <div class="deactivatemsg attention" id="deactivatemsg">@lang_are_you_sure@<br/>@lang_undone_warning@</div>
            <a href="#" id="deactivatebtn"><div class="sideboxbutton">@lang_deactivate@</div></a>
            <a href="#" class="deactivatecancelbtn" id="deactivatecancelbtn"><div class="sideboxbutton">@lang_cancel@</div></a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
<!--
    <div class="boxtitle">TIPS!</div>
    <div class="sidebox">
        <p>NICKNAME - the publicly viewable name on this site for comments</p>
        <p>EMAIL - your email address used for notifications, never publicly displayed</p>
        <p>NAME - your first and last name, not public, only for private communications</p>
        <p>INVESTOR - whether you are an accredited investor, professional investor, or otherwise permitted to invest in private companies</p>
        <p>TITLE - your position in the company, school or organization, private</p>
        <p>COMPANY - your current company, school, or organization, private</p>
        <p>PHONE - your phone country code and number, private</p>
        <p>LOCATION - where do you live or where is your company located, e.g. 12345 Sunset Blvd, San Andreas, CA, USA: private</p>
        <p>NOTIFY - whether you would like to receive an email copy of your notifications, you may still receive an email for legally required notices even if disabled</p>
        <p>CHANGE PASSWORD - password must be at least 8 characters long, cannot contain your name or username, and cannot have more than two consecutive items in sequence</p>
        <p>DEACTIVATE ACCOUNT - your entire account will be deactivated, only certain legally required information will be kept on file</p>
    </div>
-->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new EditProfileClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
