require 'digest/md5'

def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
 [
    [
      'abba[mnop]qrst', # supports TLS (abba outside square brackets).
      'abcd[bddb]xyyx', # does not support TLS (bddb is within square brackets, even though xyyx is outside square brackets).
      'aaaa[qwer]tyui', # does not support TLS (aaaa is invalid; the interior characters must be different).
      'ioxxoj[asdfgh]zxcvbn', # supports TLS (oxxo is outside square brackets, even though it's within a larger string)
    ],
    2,
  ],
  [
    %w(
      xdsqxnovprgovwzkus[fmadbfsbqwzzrzrgdg]aeqornszgvbizdm
      itgslvpxoqqakli[arktzcssgkxktejbno]wsgkbwwtbmfnddt[zblrboqsvezcgfmfvcz]iwyhyatqetsreeyhh
      pyxuijrepsmyiacl[rskpebsqdfctoqg]hbwageeiufvcmuk[wfvdhxyzmfgmcphpfnc]aotmbcnntmdltjxuusn
      mfhczaevladdsqawgp[rwabwdnwiytloldf]varesbnjnsdbsmhmsi[tyjtbpzrbfzbwlga]sznkksuymkbyxlykfqg[fyislgfghcbltaft]knrkzaldhauordwfl
      piftqfdhtumcmjmsge[qrsntvxhtfurcgcynx]oyswvuklvtmivlhen[syqhqtijyiduoxb]pdtdrhijqqzvcnl[xivmeqcwyafxvnok]jvlbkrwbgcgzaqms
      pfqiqyscrxhvtrjzt[unmovhoommbcckocp]ziwuhtfghcqhzeysdw[zmhlfonldrgkbimft]nnlbctvfpbcoqzw[zivyewjzuuvvasybded]mznpvozhzsvkdedqu
      adncdhtushtvtfcbez[rvaycmplefdvbrchc]vtviiplkpfhsyhwzz[pdpnsseaizogzvtkcq]piorguaivfpummlo
      cdgyiakhcpbibtdwm[dqmibwtfswjlfxvwe]jghsohdnnowueerunt[stsuvrwswspkgom]mmyifoverwkyjqfofhd
      luqpeubugunvgzdqk[jfnihalscclrffkxqz]wvzpvmpfiehevybbgpg[esjuempbtmfmwwmqa]rhflhjrqjbbsadjnyc
      yqdhleetfcqhdiib[eceprgdrrsmbarxdtbq]hdayiijoaaeumfwcdj
      cqqvoxzdokmgiwgcks[jqzwdkyjpbdchlt]phkfcoalnhoxnczrru
      uxpvoytxfazjjhi[qogwhtzmwxvjwxreuz]zduoybbzxigwggwu[lamifchqqwbphhsqnf]qrjdjwtnhsjqftnqsk[bsqinwypsnnvougrs]wfmhtjkysqffllakru
      jfuokpqkhmnvixa[fxfcqxfxbmhazuspg]eqfpfndvqnxluairk
      rvvyvofaygynnetjtry[kegzdkleyezldyeyn]erioueyndgksxetku[tsarhnyrbaubgmteiw]lbcsksdiqqdacutvc
      kcnplnobxleghgdvuj[xmkpquawwovbgbki]ydrgjkuwsnowlxp[otgpeovujsfeshns]vqiwhcljdyfdrgpss[mbueikaehexofmdkxtz]mbgagruljphuhapf
      dczzsivjatnsdtb[bqibajqrvbwuxqfbai]toipqjhhzoxwswm[qhcyajbtiqtvkpil]uzoshfoeofuimwkjr
      tpyvbalbljeljgih[jvwhwlaaunyiycyh]cujlqqqupambxlforvo[eswlhhjbarxdslteds]fyxrqtfcbzimodoerps[ibxdqdwuouhweuzpy]eopmknebxbkadpdc
      lpupzjmujxyptinjm[fuabibwthqibicvgd]dykosaqyoanjhbook[yfxajvdidqrxvbyd]sbulnzowfrqqvkyii
      rqzbgzdvfozqjdj[ymsvzvqjhzvzmexeko]xzuzjbrkzveydulz[jqdjbpgldsvpamfk]dfepgnmeyjnunugun
      uyfqyhnrybzytbm[ipvxhugnmquoqaunj]wdhejsfsvyurhkzbu[ucqkjfxlacfdypmvldj]mscvoriclxgvrbc[dcbnikphxidyyyuhf]tcqweefdaqypwhmsvxr
      qhzpcaxmbfnvnwktcxr[vrfrbidnjbgvrbeycgs]feuevpahnefuhxruhb[fukhbhkbqwyxergyueq]uranatwcniqfink[zhgpiqbpjcvyrduzyad]mmtbqboaahhjhssg
      jpgwqwifygprvkyvtnv[dkyxnvefvandfhkkzrm]mnxkwzpqfrxmlcmt[zxmvfefabwormvbobny]mcieumeekejrdqdono
      vqlnbtvojgdtchb[otldofiavlmzmcix]hqidiiujqigyojgrv[ozfdaqeikjttcugzudc]jcvznucmpzzwnnv[blfzwhciaomuugpmj]aabnfuksfyuzlif
      yjtasudlajobpswlde[sutivogsaeyvmbwca]nvifvaewslbeftp[pikriwclofnphifbpnm]srtjcbgjdqaesrqci[bjkdzzwsyvglijvahz]pjpcgkdyyjcwaewuha
      lgxsyzenbcjgsmix[mitplziqcskpwiqtjw]emlmmeszibngllixk
      jlscpqhpgglyyscnhj[otivpqjapmzdblqsw]ygtyjhqvwwvfgohon
      aiwoefcwoeqwextoxp[bylubaahxfxiesk]hbrtlnaixkrcfgkjbo
      wlmcvfnfjyytctu[ornmuojenqtnhbx]ztsljuxapzxyukrtrnb[vwyozabsxvhgfocvmvw]ycticvyyxubyacik[rnfjsgktvqfmdkcml]ywsfuibwwstugijcnkk
      abpxdcnbqeoeiidhpt[zpwzuygklghkvrzsogw]mdmjoojzrwdqcywsxd[jbxptisjyvgicpqnw]aanbeosfyeptpuzmrz[pasvleayajolpwhj]hsbidwxbtlfdmsahbu
      xnahkvvizpgzhrin[gbinmvooofzbjgcdbo]uitsnvqpmmlxarqcl[cewxdokvpkmoanrvvwv]kbtyedxhfkrfijx[enflewhsxrdwnjai]hxtiihnkifwudjfmcm
      acvimhsygwvhjrh[pvmhhtqztwqubpt]uzliobrctimoxeoiwlz[bduywqgtzycnjdknngb]ryitwljdrdugakt
      ymnekcaxqulhkukjx[wchabhgwvqfybkisuf]pazsmodqxwvxajwzmj
      lsixccudoihndua[vsipelrpfkhgdcnqlu]fpbarcjzbvldiukpls[joopfopddwnqnvepftt]iortrfbykllelfxjl
      yfrhdiqprjfauyzxmd[bektsogstuafoqg]rqwkjubhybwgynx[nocsrqogzkmarbrpp]aegzosyhbazgeiwwv[iqpajvjvhaimvks]wnzdupcnpsyxubos
      debonekwvzpxvybs[qrjumvswkseqjyxw]xamljdcnwsfujegc[zpfvufucwgwiylbafpt]ljpnwlwjepkkkmrz
      prdqamwjqinxgbaoadk[jhcsekzuowkdmalv]qkxdtqnnvgzthdvlnm
      vddqfnrfmbxrayhmfph[dbsadhdnaweddhn]fvwaseggzyqhybmbdxr[brelmqesxjfgkkyyufr]acdmphljtmdqbed
      xzkaadqxdyppjjbjo[jgqhvlfdunkadavlgk]guejdgxbzgyyfkctfcs[odemgpagirehrmvw]eommsvwnvwzfcdixuv
      jtzkiobrunhacbx[xvmkaeifubbqkeni]jcvpmbogikakaoeyyoh
      dmbqbsjtzvoiultxl[dqaxgsdilorzmmslys]xgbrocfkjvzykeibdi[wmpfporrraydnlbw]ijwlpgxgkqwnnwneif
    ),
    4
  ],
  [
    %w(
    abba[aaaa]asdf
    abba[baab]asdf
    abba[asdf]fdsa[baab]gtwrr
    ),
    1
  ],
  [
    %w(
    aaaa[abba]asdf
    aaaa[aaaa]asdf
    asdf[aaaa]abba
    ),
    1
  ],
  [
    %w(
    xyyxasdfaaaafewd[pwef]asdf
    xxxxasdfabbafewd[pwef]asdf
    xxxxasdfabewd[pwef]asdf
    ),
    2
  ],
]

ABBA_REGEX = /.*(([a-z])([a-z])\3\2).*/
ABBA_SAME_CHAR_REGEX = /^([a-z])\1{3}$/

def abba?(part)
  ABBA_REGEX =~ part && ABBA_SAME_CHAR_REGEX !~ Regexp.last_match[1]
end

# def unpack_address(address)
#   address
#     .split(/[\[\]]/)
#     .each_with_index
#     .partition { |_part, index| index.even? }
#     .map { |group| group.map(&:first) }
# end
# 
# def supports_tls?(address)
#   (non_hypernet_parts, hypernet_parts) = unpack_address(address)
#   at_least_one_valid = non_hypernet_parts.any?(&method(:abba?))
#   none_invalid = hypernet_parts.all? { |part| !abba?(part) }
#   at_least_one_valid && none_invalid
# end
#

REPEATS_REGEX = /([a-z])\1{3}/
HYPERNET_INVALIDATOR_REGEX = /\[[a-z]*(([a-z])([a-z])\3\2)[a-z]*\]/

def remove_invalid_repeats(address)
  address.gsub(REPEATS_REGEX, '')
end

def not_invalidated?(address)
  HYPERNET_INVALIDATOR_REGEX !~ address
end

def valid?(address)
  ABBA_REGEX =~ address
end

def solve(input)
  #input.select(&method(:supports_tls?)).tap{|x| puts(*x.join("\n"))}.size
  #input.select(&method(:supports_tls?)).size
  input
    .map(&method(:remove_invalid_repeats))
    .select(&method(:not_invalidated?))
    .select(&method(:valid?))
    .size
end

def test(label, input, expected)
  $stdout.write "[#{label}]: expected #{expected}"
  actual = solve(input)
  $stdout.write ", actual #{actual.inspect}"
  if actual == expected
    $stdout.write " ✅"
  else
    $stdout.write " 💥"
  end
  puts
end

def run
  TESTS.each_with_index do |(input, expected), index|
    test("Test #{index}", input, expected)
  end
  puts "[Puzzle]: #{solve(PUZZLE)}"
end

run
