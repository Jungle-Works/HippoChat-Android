package com.hippo.tickets;

import com.hippo.utils.fileUpload.FileuploadModel;

public interface AttachmentSelectedTicketListener {
    void onAttachmentListener(FileuploadModel fileuploadModel,int attachmentPosition);
    void onAttachmentSelected(int attachmentPosition);
}
