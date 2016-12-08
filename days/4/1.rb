def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    convert(%q{
      aaaaa-bbb-z-y-x-123[abxyz]
      a-b-c-d-e-f-g-h-987[abcde]
      not-a-real-room-404[oarel]
      totally-real-room-200[decoy]
    }.lines),
    1514,
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

def add_sector_id(sum, room_name)
  match = REGEX.match(room_name)
  name = match[1]
  sector_id = match[2]
  checksum = match[3]
  if valid_room?(name, checksum)
    sum + sector_id.to_i
  else
    sum
  end
end

def solve(input)
  input.reduce(0, &method(:add_sector_id))
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
