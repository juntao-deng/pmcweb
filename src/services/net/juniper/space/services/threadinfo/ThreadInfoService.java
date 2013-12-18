package net.juniper.space.services.threadinfo;

import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;

public interface ThreadInfoService {
	public ThreadInfoDump[] getThreadInfos();
}
