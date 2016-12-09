def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    convert(%q{
      10  5  25
      150 72 24
      2   3  4
      12  13 14
      14  13 12
      4   2  3
    }.lines),
    4
  ]
]

def valid?(values)
  values.permutation.to_a.all? do |(a, b, c)|
    (a + b) > c
  end
end

def solve(input)
  input.reduce(0) do |sum, line|
    values = line.split(/\s+/).map(&:to_i)
    valid?(values) \
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
