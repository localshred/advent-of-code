require 'digest/md5'

PUZZLE = 'abbhdwsy'

TESTS = [
  [ 'abc', '18f47a30' ]
]

FIVE_ZEROES = '00000'

def get_password_char(door_id, index)
  digest = Digest::MD5.hexdigest("#{door_id}#{index}")
  if digest[0..4] == FIVE_ZEROES
    digest[5]
  else
    nil
  end
end

def solve(door_id)
  index = 0
  password = ''
  while password.size < 8
    char = get_password_char(door_id, index)
    unless char.nil?
      puts "\nFound char #{char} at #{index}"
      password << char 
    end
    putc '.' if index % 10000 == 0
    index += 1
  end
  password
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
