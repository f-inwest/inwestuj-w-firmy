`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-media-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
include(company-not-found.m4)
`
<div class="banner genericbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_wizard_title@</div>
            <div class="welcometext">@lang_project_media@</div>
        </span>
    </div>
</div>

<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->

<!-- end banner -->

<div class="initialhidden" id="newlistingmediawrapper">

<!-- left column -->
<div class="span-16">

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_video@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
                @lang_video_help@
            </p>
        </div>
    </div>
    <div class="boxpanel videopanel" style="padding-top: 10px; overflow: visible; width: 627px; height: inherit;">
        <div class="formitem sideinfoitem clear" style="padding-left: 15px">
            <span class="inputfield">
                <input class="text mediainputlink videourl" style="width: 548px !important" type="text" maxlength="255" name="video" id="video" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_instructions@</label>
                <br />
                @lang_video_help@
            </p>
            <span class="videoinputicon">
                <div id="videoicon"></div>
            </span>
        </div>
        <div class="formitem clear" style="padding-left: 15px; padding-bottom: 5px;">
            <span class="uploadinfo" id="videomsg">
                @lang_video_msg@
            </span>
        </div>
	    <div class="videocontainer">
      	    <iframe width="627" height="357" id="videoiframe" src="" frameborder="0" allowfullscreen></iframe>
	    </div>
    </div>

    <div class="boxtitle basicstitle header-nobottom" style="margin-top: 20px;">
        <span class="titletext">@lang_website@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
                @lang_website_help@
            </p>
        </div>
    </div>

    <div class="boxpanel" style="overflow: visible; height: 45px;">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="website">@lang_website@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="website" id="website" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_instructions@</label>
                <br />
                @lang_website_help@
            </p>
            <span class="inputicon">
                <div id="websiteicon"></div>
            </span>
        </div>
    </div>

    <div class="formitem clear">
        <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
    </div>

    <div>
        <div class="formitem clear">
           <a href="#" class="nextbuttonlink">
                <span class="push-13 span-3 inputbutton">
                    @lang_next@
                </span>
            </a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <a href="#" class="backbuttonlink">
        <span class="push-1 span-3 inputbutton backbutton" style="margin-bottom: 0;">
                &lt;&lt; @lang_back@
        </span>
    </a>
</div>
<!-- end right column -->

</div>
'
include(new-listing-bottom-buttons.m4)
`

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingmediapage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
