package net.juniper.jmp.tags.restful.impl;

import java.util.ArrayList;
import java.util.List;

import net.juniper.jmp.tags.model.TagMO;
import net.juniper.jmp.tags.restful.TagsRestService;
public class TagsRestServiceImpl implements TagsRestService{
	@Override
	public TagMO[] getTags() {
		List<TagMO> tagList = new ArrayList<TagMO>();
		TagMO pubTag = new TagMO();
		pubTag.setId(0);
		pubTag.setTagname("Public");
		pubTag.setFolder(true);
		tagList.add(pubTag);
		
		TagMO priTag = new TagMO();
		priTag.setId(1);
		priTag.setTagname("Private");
		priTag.setFolder(true);
		tagList.add(priTag);
		return tagList.toArray(new TagMO[0]);
	}
}
