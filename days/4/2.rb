def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    convert(%q{
      qzmt-zixmtkozy-ivhz-343[zimth]
      totally-real-room-200[decoy]
    }.lines),
    [['very encrypted name', 343]],
  ]
]

REGEX = /^(.+?)-(\d+)\[([^\]]+)\]$/

def make_checksum(name)
  clean_name = name.gsub(/[^a-zA-Z]+/, '')
  initial = Hash.new { |hash, key| hash[key] = 0 }
  counted_chars = clean_name.chars.reduce(initial) do |chars, char|
    chars[char] += 1
    chars
  end

  counted_chars
    .to_a
    .sort { |(char1, count1), (char2, count2)| [count2, -char2.ord] <=> [count1, -char1.ord] }
    .take(5)
    .map(&:first)
    .join
end

def valid_room?(name, checksum)
  make_checksum(name) == checksum
end

def rotate(char, char_increment)
  adjusted_code = char.ord - 97
  incremented = adjusted_code + char_increment
  incremented -= 26 if incremented >= 26
  (incremented + 97).chr
end

def decrypt(encrypted, rounds)
  char_increment = rounds % 26
  encrypted.chars.map do |char|
    if char == '-'
      ' '
    else
      rotate(char, char_increment)
    end
  end.join
end

def validate_and_decrypt(valid_rooms, room_name)
  match = REGEX.match(room_name)
  name = match[1]
  sector_id = match[2].to_i
  checksum = match[3]
  if valid_room?(name, checksum)
    real_name = decrypt(name, sector_id)
    valid_rooms << [real_name, sector_id]
  else
    valid_rooms
  end
end

def solve(input, filter = true)
  decrypted = input
    .reduce([], &method(:validate_and_decrypt))

  if filter
    decrypted
      .select { |(name, _)| name =~ /north\s*pole/i }
  else
    decrypted
  end
end

def test(label, input, expected)
  $stdout.write "[#{label}]: expected #{expected}"
  actual = solve(input, false)
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
