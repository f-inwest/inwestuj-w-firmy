`
    <div class="boxtitle boxtitleside clear boxtitleside-logo header-nobottom">@lang_upload_logo@</span></div>
    <div class="uploadbox uploadbox-logo">
        <div class="formitem">
            <span class="uploadinfo">@lang_upload_logo_desc@</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="logo_url" id="logo_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="logo_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="logouploadform" method="post" enctype="multipart/form-data" target="logouploadiframe" action="#">
                    <input class="text picinputlink" id="logouploadfile" name="LOGO" size="16" type="file"></input>
                    <iframe id="logouploadiframe" name="logouploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="logomsg">@lang_upload_logo_short_desc@</span>
        </div>
    </div>

    <div class="boxtitle boxtitleside clear header-nobottom">@lang_upload_image@ <span id="picnum">1</span></div>
    <div class="uploadbox">
        <div class="formitem">
            <span class="uploadinfo">@lang_upload_image_desc@</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="pic_url" id="pic_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="pic_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="picuploadform" method="post" enctype="multipart/form-data" target="picuploadiframe" action="#">
                    <input class="text picinputlink" id="picuploadfile" name="PIC1" size="16" type="file"></input>
                    <iframe id="picuploadiframe" name="picuploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="picmsg">@lang_upload_image_short_desc@</span>
        </div>
    </div>
'