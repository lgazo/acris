package sk.seges.acris.showcase.mora.shared.domain.api.common;

import sk.seges.acris.showcase.mora.shared.domain.api.CommentData;

public interface HasComment {

	CommentData getComment();
	
	void setComment(CommentData comment);
}
