def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    convert(%q{
      10 150 2
      5  72  3
      32 24  4
      12 14  4
      13 13  2
      14 12  3
    }.lines),
    4
  ]
]

def valid?(values)
  values.permutation.to_a.all? do |(a, b, c)|
    ret = (a + b) > c
    puts "(#{a} + #{b}) > #{c} -> #{ret.inspect}"
    ret
  end
end

def solve(input)
  parsed = input.map do |line|
    line.split(/\s+/).map(&:to_i)
  end

  rotated = parsed.each_slice(3).map do |((a1, a2, a3), (b1, b2, b3), (c1, c2, c3))|
    [
      [a1, b1, c1],
      [a2, b2, c2],
      [a3, b3, c3],
    ]
  end

  rotated.flatten(1).reduce(0) do |sum, line|
    valid?(line) \
      ? sum + 1 \
      : sum
  end
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
