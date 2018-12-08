import java.util.LinkedList;

/* >>>>>>>>>>>> Used Variables : <<<<<<<<<<<<<<<
 * 
 * - m, w and n are number of men groups, number of women groups and the total number of individuals
 * - numSingleMen will be the number of single men
 * - mar is the array presenting the final result of the algorithm
 * - numSingleMenByGroup and numSingleWomenByGroup represent the number of single men/women by group and will be refreshed in every step
 * - worstGroupWomen is an array representing during all the algorithm the position of the worst man to a women of a given group according to its preference list , for example if worstGroupWomen[j] == i 
 * than womenPrefs[j][i] is the less preferred man for the group j who is married to a women of this group
 * - lastBlackListed is an array to keep track of the last black listed women for the man i since for a man i we go through his preference list looking for a group where he can find a women verifying the conditions 
 * so if we already black listed menPrefs[i][0], menPrefs[i][1] ... menPrefs[i][k] means that we did not find women verifying the conditions in these groups lastBlackListed[m] will take the value of k and the next time 
 * we start looking in from the group menPrefs[i][k+1] ... etc.
 * - prefOrdre is and array of w x m size witch stores the preference order of a man i in the women j is preference list means if prefOrdre[j][i] == 5 for example than i is the 5th most preferred man to j (this array 
 * will not change during the algorithm and it is needed so that we do not have to calculate the order of a man i in j's preference list to compare it with worstGroupWomen[j]
 * these two lists will be refreshed during the algorithm in a constant time
 * - notNULL is a list of men with at least one man unengaged
 * 
 * >>>>>>>>>>>> The algorithm: <<<<<<<<<<<<<<
 * 
 * - we chose i a group of men with at least one unengaged man
 * - we chose j that is the most preferable group to i (not black listed) and we check that :
 * ---> there is unengaged women in j (OK)
 * ---> there is no unengaged women in j and the worstGroupWomen[j] is bigger than the ordre of preference of i (prefOrdreOfi) means there are women in j that are engaged to men (in group iPrime) less attractive than i (OK)
 * ---> we black list j if none of the two conditions above is OK
 * - we do breakups and engagements and refresh variables
  */

public class StableMatching implements StableMatchingInterface {
	public int[][] constructStableMatching(int[] menGroupCount, int[] womenGroupCount, int[][] menPrefs,
			int[][] womenPrefs) {

		////////////////// Variables declaration and initialization ///////////////////

		int m = menGroupCount.length;
		int w = womenGroupCount.length;
		int n = 0;
		for (int count = 0; count < menGroupCount.length; count++)
			n += menGroupCount[count];
		int numSingleMen = n;

		int[][] mar = new int[m][w];

		int[] numSingleMenByGroup = menGroupCount;
		int[] numSingleWomenByGroup = womenGroupCount;
		int[] worstGroupWomen = new int[w];

		int[] lastBlackListed = new int[m];
		for (int count = 0; count < m; count++)
			lastBlackListed[count] = -1;

		int[][] prefOrdre = new int[w][m];
		for (int count = 0; count < w; count++)
			for (int count2 = 0; count2 < m; count2++)
				prefOrdre[count][womenPrefs[count][count2]] = count2;

		LinkedList<Integer> notNULL = new LinkedList<>();
		for (int k = 0; k < m; k++) {
			if (numSingleMenByGroup[k] > 0)
				notNULL.add(k);
		}

		/////////////////// Algorithm /////////////////////
		
		while (numSingleMen != 0) {
			
			int i = notNULL.poll();

			// Looking for a group of women j verifing the conditions and for an iPrime
			// which is the worst group maried to j and less attractive than i
			// when there is no unengaged women in j (iPrime takes -1 in the oppisite case).
			int iPrime = -1;
			int j = 0;
			int prefOrdreOfi = 0;

			for (int count = lastBlackListed[i] + 1; count < w; count++) {
				j = menPrefs[i][count];
				prefOrdreOfi = prefOrdre[j][i];
				if (numSingleWomenByGroup[j] > 0)
					break;
				else if (numSingleWomenByGroup[j] == 0) {
					if (worstGroupWomen[j] > prefOrdreOfi) {
						iPrime = womenPrefs[j][worstGroupWomen[j]];
						break;
					} else
						lastBlackListed[i]++;
				}
			}

			// now that we found j and iPrime and i let's do necessary breakups/engagements
			// and refresh variables needing to be refreshed like notNULL,
			// worstGroupWomen ...etc.
			int a = numSingleMenByGroup[i];
			int b = (iPrime == -1) ? numSingleWomenByGroup[j] : mar[iPrime][j];
			int min = (a > b) ? b : a;

			// Case 1 : there is unengaged women in the group j (means iPrime = -1)
			if (iPrime == -1) {
				if (prefOrdreOfi > worstGroupWomen[j])
					worstGroupWomen[j] = prefOrdreOfi;
				numSingleMen -= min;
				numSingleMenByGroup[i] -= min;
				numSingleWomenByGroup[j] -= min;
				mar[i][j] += min;

			}
			// Case 2 : there is no unengaged women left in j so iPrime != -1 and we need to
			// do necessary breakups
			else {
				numSingleMenByGroup[iPrime] += min;
				numSingleMenByGroup[i] -= min;
				mar[iPrime][j] -= min;
				mar[i][j] += min;

				// refreshing worstGroupWomen
				if (b == min) {
					for (int count = worstGroupWomen[j] - 1; count >= 0; count--) {
						if (mar[womenPrefs[j][count]][j] > 0) {
							worstGroupWomen[j] = count;
							break;
						}
					}
				}

				// refreshing notNULL
				if (numSingleMenByGroup[iPrime] > 0)
					notNULL.add(iPrime);
			}

			// refreshing notNULL
			if (numSingleMenByGroup[i] > 0)
				notNULL.add(i);

		}
		return mar;

	}
}