require 'digest/md5'

PUZZLE = 'abbhdwsy'

TESTS = [
  [ 'abc', '05ace8e3' ]
]

FIVE_ZEROES = '00000'
NUMBERS_ONLY = /^[0-7]$/

def valid_position?(position)
  NUMBERS_ONLY =~ position
end

def get_password_char(door_id, index, password_size)
  digest = Digest::MD5.hexdigest("#{door_id}#{index}")
  key = digest[0..4]
  position = digest[5]
  char = digest[6]
  if key == FIVE_ZEROES && valid_position?(position)
    [position.to_i, char]
  else
    nil
  end
end

def solve(door_id)
  index = 0
  password_size = 8
  password = Array.new(password_size)
  while password.compact.size < password_size
    result = get_password_char(door_id, index, password_size)
    unless result.nil?
      position = result[0]
      char = result[1]
      if password[position].nil?
        puts "\nFound position #{position} char #{char} at #{index}"
        password[position] = char
      end
    end
    putc '.' if index % 10000 == 0
    index += 1
  end
  password.join
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
