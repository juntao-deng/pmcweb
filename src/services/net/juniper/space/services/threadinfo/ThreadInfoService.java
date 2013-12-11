package net.juniper.space.services.threadinfo;

import java.lang.management.ThreadInfo;
import java.util.List;

public interface ThreadInfoService {
	public List<ThreadInfo> getThreadInfos();
}
