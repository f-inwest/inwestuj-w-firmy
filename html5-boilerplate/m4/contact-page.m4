`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="contact-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner contactbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_contact_title@</div>
            <div class="welcometext">@lang_contact_desc@</div>
        </span>
    </div>
</div>

<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="initialhidden wrapper">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">@lang_contact_us@</div>
    <div class="boxpanel">
        <div class="formitem">
            <span class="inputlabel">@lang_question@</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="question" id="question"></input>
            </span>
            <span class="inputicon">
                <div id="questioncheckboxicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">@lang_details@</span>
            <span class="inputfield">
                <textarea class="textarea inputwidetext" cols="20" rows="5" name="details" id="details"></textarea>
            </span>
            <span class="inputicon">
                <div id="detailscheckboxicon"></div>
            </span>
        </div>
<!--
        <div class="formitem clear">
            <span class="inputlabel">LINK</span>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="linkurl" id="linkurl"></input>
            </span>
            <span class="inputicon">
                <div class="checkboxredicon"></div>
            </span>
        </div>

        recaptcha section
            public key: 6LdaiMsSAAAAAM1nDtyXO1iXqRc15_qZ0D81_s0K
            private key: 6LdaiMsSAAAAAC3TK-RYDijl_mYCfeWsdIP-JZ02

        <div class="formitem clear">
            <span class="inputlabel"></span>
            <span class="inputcaptcha">
  	        <script type="text/javascript" src="https://www.google.com/recaptcha/api/challenge?k=6LdaiMsSAAAAAM1nDtyXO1iXqRc15_qZ0D81_s0K"></script>
  	        <noscript>
                <iframe src="https://www.google.com/recaptcha/api/noscript?k=6LdaiMsSAAAAAM1nDtyXO1iXqRc15_qZ0D81_s0K" height="300" width="500" frameborder="0"></iframe><br>
                <textarea name="recaptcha_challenge_field" rows="3" cols="40"></textarea>
                <input type="hidden" name="recaptcha_response_field" value="manual_challenge">
            </noscript>
            </span>
	    </div>

-->

        <div class="formitem clear">
	        <span class="inputlabel"></span>
            <span class="span-9 inputmsg" id="submitmsg">&nbsp;</span>
            <span class="span-3 inputbutton" id="submitcontactbutton">@lang_submit@</span>
            <span class="span-6 inprogress inputmsg initialhidden" id="confirmmsg">@lang_send_confirm@</span>
            <a href="#" id="sendbuttonlink">
                <span class="span-3 inputbutton initialhidden" id="sendbutton">@lang_send@</span>
            </a>
            <span class="span-3 inputbutton initialhidden" id="cancelbutton">@lang_cancel@</span>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
<!--
    <div class="boxtitle">A PIECE OF THE ACTION</div>
    <div class="sidebox">
        <p>
With Inwestuj w Firmy, you&rsquo;re plugged into the pulse of the startup community.
Keep up to date on all the latest startups.  Post your own startup as an
entrepreneuer, getting feedback and exposure to investors worldwide.  As an
accredited investor, you can bid for a piece of the action.
        </p>
    </div>
-->
    <div class="boxtitle header-sidebox" id="listingstitle">@lang_projects@</div>

    <!-- companydiv -->
    <div id="companydiv">
        <div class="span-8 preloaderside">
            <div class="preloaderfloater"></div>
            <div class="preloadericon"></div>
        </div>
    </div>
    <!-- end companydiv -->

</div> <!-- end right column -->
</div> <!-- end wrapper -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/contactpage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
