package ext.dataMove.export.user;

import java.io.File;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;

public class ExportGroupGroup implements RemoteAccess {

	public static final String xmlHeader = "#GroupGroup,user,parentGroupName,subGroupName,parentDirectoryService,childDirectoryService";

	public static String GROUPGROUP_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "GroupGroup";
	public static String GROUPGROUP_CSV_FILE_PATH = GROUPGROUP_FILE_PATH + File.separator + "GroupGroup.csv";

	public static void process(ArgsInfo argsInfo) throws Exception {

		List group_list = ExportGroup.searchGroup();
		int total = group_list.size();
		System.out.println("Search WTGroup total:" + total);
		StringBuffer log = new StringBuffer();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String logFilePath = GROUPGROUP_FILE_PATH + File.separator + "exportgroupgroup_" + System.currentTimeMillis() + ".log";
		log.append(currentTime + "  开始处理\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
		for (int i = 0; i < total; i++) {
			boolean existCsvFile = false;
			File csvFile = new File(GROUPGROUP_CSV_FILE_PATH);
			if (csvFile.exists()) {
				existCsvFile = true;
			}

			StringBuffer sb = new StringBuffer();
			if (!existCsvFile) {
				sb.append(xmlHeader + "\r\n");
			}

			WTGroup group = (WTGroup) group_list.get(i);
			String groupName = group.getName();
			StringBuffer msg = new StringBuffer();
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + " 开始处理" + (i + 1) + "/" + total + " 个组" + groupName + " \r\n");
			List sgroup_list = getGroupMembersOfGroup(group);
			for (int j = 0; j < sgroup_list.size(); j++) {
				WTGroup sgroup = (WTGroup) sgroup_list.get(j);
				String sgroupName = sgroup.getName();
				sb.append("GroupGroup,");
				sb.append(",");
				sb.append(groupName + ",");//parentGroupName
				sb.append(sgroupName + ",");//subGroupName
				sb.append(",");
				sb.append(",\r\n");
			}
			ExportUtil.writeTxt(GROUPGROUP_CSV_FILE_PATH, sb.toString());
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + " 结束处理" + (i + 1) + "/" + total + " 个组" + groupName + " \r\n");
			ExportUtil.writeTxt(logFilePath, msg.toString());
		}
		log = new StringBuffer();
		log.append(currentTime + "  结束处理\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
	}

	/**
	 * 
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static List getGroupMembersOfGroup(WTGroup group) throws Exception {
		if (group == null) {
			return null;
		}
		List groups = new ArrayList();
		//false 时，只找第一层组或者用户；true 时，递归向下寻找所有
		Enumeration member = OrganizationServicesHelper.manager.members(group, false);//group.members();
		try {
			while (member.hasMoreElements()) {
				WTPrincipal principal = (WTPrincipal) member.nextElement();
				if (principal instanceof WTGroup) {
					groups.add((WTGroup) principal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}

}
