require 'digest/md5'

def convert(input)
  input
    .map(&:strip)
    .select { |line| !line.empty? }
end

PUZZLE = convert(File.readlines(File.expand_path("input.txt", __dir__)))

TESTS = [
  [
    convert(%q{
      eedadn
      drvtee
      eandsr
      raavrd
      atevrs
      tsrnev
      sdttsa
      rasrtv
      nssdts
      ntnada
      svetve
      tesnvt
      vntsnd
      vrdear
      dvrsen
      enarar
    }.lines),
    'easter',
  ],
]

def count_chars(accumulator, line)
  line.chars.each_with_index.reduce(accumulator) do |inner, (char, index)|
    inner[index][char] += 1
    inner
  end
end

def most_frequent_char(accumulator, (index, chars))
  accumulator[index] = chars.max_by { |char, count| count }.first
  accumulator
end

def solve(input)
  initial = Hash.new do |outer, index|
    outer[index] = Hash.new do |inner, char|
      inner[char] = 0
    end
  end

  input
    .reduce(initial, &method(:count_chars))
    .reduce(Array.new(input.first.size), &method(:most_frequent_char))
    .join
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
