package net.juniper.space.services.threadinfo;

import net.juniper.jmp.monitor.info.dump.ThreadInfoDump;

public interface ThreadInfoService {
	public ThreadInfoDump[] getThreadInfos();
}
